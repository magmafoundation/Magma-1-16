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

package org.magmafoundation.magma.patcher.impl;


import org.magmafoundation.magma.patcher.Patcher;
import org.magmafoundation.magma.patcher.Patcher.PatcherInfo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * EssentialsPatcher
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 29/07/2020 - 03:58 am
 */
@PatcherInfo(name = "Essentials", description = "Fixes for Essentials")
public class EssentialsPatcher extends Patcher {

    @Override
    public byte[] transform(String className, byte[] clazz) {
        if (className.equals("com.earth2me.essentials.Settings")) {
            return itemDBPatch(clazz);
        }
        return clazz;
    }

    private byte[] itemDBPatch(byte[] clazz) {
        ClassReader reader = new ClassReader(clazz);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        for (MethodNode methodNode : node.methods) {
            if (methodNode.name.equals("getItemDbType") && methodNode.desc.equals("()Ljava/lang/String;")) {
                InsnList insnList = new InsnList();
                insnList.add(new LdcInsnNode("csv"));
                insnList.add(new InsnNode(Opcodes.ARETURN));
                methodNode.instructions = insnList;
            }
        }

        ClassWriter writer = new ClassWriter(0);
        node.accept(writer);
        return writer.toByteArray();
    }


}
