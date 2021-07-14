/*
 * Magma Server
 * Copyright (C) 2019-2021.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.magmafoundation.magma.remapper.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ListIterator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.provider.JointProvider;
import org.magmafoundation.magma.remapper.v2.proxy.ProxyClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * ReflectionTransformer
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 13/07/2021 - 08:44 pm
 */
public class ReflectionTransformer {

	public static final HashMap<String, String> classReverseMapping = Maps.newHashMap();
	public static final Multimap<String, String> methodReverseMapping = ArrayListMultimap.create();
	public static final Multimap<String, String> fieldReverseMapping = ArrayListMultimap.create();
	public static final Multimap<String, String> methodFastMapping = ArrayListMultimap.create();
	public static JarMapping jarMapping;
	public static MagmaRemapper remapper;

	public static void init() {
		jarMapping = MappingLoader.loadMapping();
		JointProvider provider = new JointProvider();
		provider.add(new ClassInheritanceProvider());
		jarMapping.setFallbackInheritanceProvider(provider);
		remapper = new MagmaRemapper(jarMapping);

		jarMapping.classes.forEach((k, v) -> classReverseMapping.put(v, k));
		jarMapping.methods.forEach((k, v) -> methodReverseMapping.put(v, k));
		jarMapping.fields.forEach((k, v) -> fieldReverseMapping.put(v, k));
		jarMapping.methods.forEach((k, v) -> methodFastMapping.put(k.split("\\s+")[0], k));

		try {
			Class.forName("org.magmafoundation.magma.remapper.v2.proxy.ProxyMethodHandle");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static byte[] transform(byte[] code) {
		ClassReader reader = new ClassReader(code);
		ClassNode node = new ClassNode();
		reader.accept(node, 0);

		boolean remapCL = false;
		Class<?> remappedSuperClass = ReflectionMapping.getSuperClassTarget(node.superName);
		if (remappedSuperClass != null) {
			if (remappedSuperClass == ProxyClassLoader.class) {
				ReflectionMapping.addVirtualMethodTarget(node.name + ";defineClass", ProxyClassLoader.class);
			}
			node.superName = Type.getInternalName(remappedSuperClass);
			remapCL = true;
		}

		for (MethodNode method : node.methods) {
			ListIterator<AbstractInsnNode> insnIterator = method.instructions.iterator();
			while (insnIterator.hasNext()) {
				AbstractInsnNode next = insnIterator.next();

				if (next instanceof TypeInsnNode && next.getOpcode() == Opcodes.NEW) {
					TypeInsnNode insn = (TypeInsnNode) next;
					Class<?> remappedClass = ReflectionMapping.getSuperClassTarget(insn.desc);
					if (remappedClass != null) {
						insn.desc = Type.getInternalName(remappedClass);
						remapCL = true;
					}
				}

				if (next instanceof MethodInsnNode) {
					MethodInsnNode insn = (MethodInsnNode) next;
					switch (insn.getOpcode()) {
					case Opcodes.INVOKEVIRTUAL:
						remapVirtual(insn);
						break;
					case Opcodes.INVOKESTATIC:
						remapStatic(insn);
						break;
					case Opcodes.INVOKESPECIAL:
						if (remapCL) remapSuperClass(insn);
						remapScriptEngineManager(insn, method);
						break;
					}
				}
			}
		}

		ClassWriter writer = new ClassWriter(0);
		node.accept(writer);
		return writer.toByteArray();
	}

	public static void remapStatic(MethodInsnNode insn) {
		Class<?> remappedClass = ReflectionMapping.getStaticMethodTarget((insn.owner + ";" + insn.name));
		if (remappedClass != null) {
			insn.owner = Type.getInternalName(remappedClass);
		}
	}

	public static void remapVirtual(MethodInsnNode insn) {
		Class<?> remappedClass = ReflectionMapping.getVirtualMethodToStaticTarget((insn.owner + ";" + insn.name));
		if (remappedClass != null) {
			Type returnType = Type.getReturnType(insn.desc);
			ArrayList<Type> args = new ArrayList<>();
			args.add(Type.getObjectType(insn.owner));
			args.addAll(Arrays.asList(Type.getArgumentTypes(insn.desc)));

			insn.setOpcode(Opcodes.INVOKESTATIC);
			insn.owner = Type.getInternalName(remappedClass);
			insn.desc = Type.getMethodDescriptor(returnType, args.toArray(new Type[0]));
		} else {
			remappedClass = ReflectionMapping.getVirtualMethodTarget((insn.owner + ";" + insn.name));
			if (remappedClass != null) {
				insn.name += "Remap";
				insn.owner = Type.getInternalName(remappedClass);
			}
		}
	}

	private static void remapSuperClass(MethodInsnNode insn) {
		Class<?> remappedClass = ReflectionMapping.getSuperClassTarget(insn.owner);
		if (remappedClass != null && insn.name.equals("<init>")) {
			insn.owner = Type.getInternalName(remappedClass);
		}
	}

	private static void remapScriptEngineManager(MethodInsnNode insn, MethodNode node) {
		if (insn.owner.equals("javax/script/ScriptEngineManager") && insn.desc.equals("()V") && insn.name.equals("<init>")) {
			insn.desc = "(Ljava/lang/ClassLoader;)V";
			node.instructions.insertBefore(insn, new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/ClassLoader", "getSystemClassLoader", "()Ljava/lang/ClassLoader;"));
			node.maxStack++;
		}
	}
}
