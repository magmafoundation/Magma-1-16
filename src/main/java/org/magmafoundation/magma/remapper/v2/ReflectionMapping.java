package org.magmafoundation.magma.remapper.v2;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.magmafoundation.magma.Magma;
import org.magmafoundation.magma.remapper.v2.proxy.ProxyClassLoader;
import org.magmafoundation.magma.remapper.v2.proxy.ProxyMethodHandle;
import org.magmafoundation.magma.remapper.v2.proxy.ProxyReflection;
import org.magmafoundation.magma.remapper.v2.proxy.ProxyURLClassLoader;

/**
 * ReflectionMapping
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 13/07/2021 - 08:27 pm
 */
public class ReflectionMapping {

	private static final Set<String> remapPackages = Sets.newHashSet();
	private static final Map<String, Class<?>> remapStaticMethod = new HashMap<>();
	private static final Map<String, Class<?>> remapVirtualMethod = Maps.newHashMap();
	private static final Map<String, Class<?>> remapVirtualMethodToStatic = new HashMap<>();
	private static final Map<String, Class<?>> remapSuperClass = new HashMap<>();

	static {

		remapPackages.add("net.minecraft.server." + Magma.getBukkitVersion());
		remapPackages.add("org.bukkit.craftbukkit.");

		remapStaticMethod.put("java/lang/Class;forName", ProxyReflection.class);
		remapStaticMethod.put("java/lang/invoke/MethodType;fromMethodDescriptorString", ProxyMethodHandle.class);

		remapVirtualMethodToStatic.put("java/lang/Class;getField", ProxyReflection.class);
		remapVirtualMethodToStatic.put("java/lang/Class;getDeclaredField", ProxyReflection.class);
		remapVirtualMethodToStatic.put("java/lang/Class;getMethod", ProxyReflection.class);
		remapVirtualMethodToStatic.put("java/lang/Class;getDeclaredMethod", ProxyReflection.class);
		remapVirtualMethodToStatic.put("java/lang/Class;getSimpleName", ProxyReflection.class);
		remapVirtualMethodToStatic.put("java/lang/Class;getName", ProxyReflection.class);
		remapVirtualMethodToStatic.put("java/lang/Class;getDeclaredMethods", ProxyReflection.class);
		remapVirtualMethodToStatic.put("java/lang/reflect/Field;getName", ProxyReflection.class);
		remapVirtualMethodToStatic.put("java/lang/reflect/Method;getName", ProxyReflection.class);
		remapVirtualMethodToStatic.put("java/lang/ClassLoader;loadClass", ProxyReflection.class);
		remapVirtualMethodToStatic.put("java/lang/invoke/MethodHandles$Lookup;findStatic", ProxyMethodHandle.class);
		remapVirtualMethodToStatic.put("java/lang/invoke/MethodHandles$Lookup;findVirtual", ProxyMethodHandle.class);
		remapVirtualMethodToStatic.put("java/lang/invoke/MethodHandles$Lookup;findSpecial", ProxyMethodHandle.class);
		remapVirtualMethodToStatic.put("java/lang/invoke/MethodHandles$Lookup;findGetter", ProxyMethodHandle.class);
		remapVirtualMethodToStatic.put("java/lang/invoke/MethodHandles$Lookup;findSetter", ProxyMethodHandle.class);
		remapVirtualMethodToStatic.put("java/lang/invoke/MethodHandles$Lookup;findStaticGetter", ProxyMethodHandle.class);
		remapVirtualMethodToStatic.put("java/lang/invoke/MethodHandles$Lookup;findStaticSetter", ProxyMethodHandle.class);
		remapVirtualMethodToStatic.put("java/lang/invoke/MethodHandles$Lookup;unreflect", ProxyMethodHandle.class);

		remapSuperClass.put("java/net/URLClassLoader", ProxyURLClassLoader.class);
		remapSuperClass.put("java/lang/ClassLoader", ProxyClassLoader.class);
	}

	public static boolean isNMSPackage(String className) {
		return className.replace("/", ".").startsWith("net.minecraft.server." + Magma.getBukkitVersion());
	}

	public static String getNMSPackage() {
		return "net.minecraft.server." + Magma.getBukkitVersion();
	}


	public static boolean isNeedRemapClass(String className) {
		className = className.replace("/", ".");
		for (String remapPackage : remapPackages) {
			if (className.startsWith(remapPackage)) return true;
		}
		return false;
	}

	public static Class<?> getStaticMethodTarget(String original) {
		return remapStaticMethod.get(original);
	}

	public static Class<?> getVirtualMethodTarget(String original) {
		return remapVirtualMethod.get(original);
	}

	public static Class<?> getVirtualMethodToStaticTarget(String original) {
		return remapVirtualMethodToStatic.get(original);
	}

	public static Class<?> getSuperClassTarget(String original) {
		return remapSuperClass.get(original);
	}

	public static void addVirtualMethodTarget(String original, Class<?> target) {
		remapVirtualMethod.put(original, target);
	}

}
