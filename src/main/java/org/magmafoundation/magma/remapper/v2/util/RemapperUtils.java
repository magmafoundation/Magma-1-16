package org.magmafoundation.magma.remapper.v2.util;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.md_5.specialsource.JarRemapper;
import org.magmafoundation.magma.Magma;
import org.magmafoundation.magma.remapper.v2.ReflectionTransformer;
import org.objectweb.asm.Type;

/**
 * RemapperUtils
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 13/07/2021 - 08:53 pm
 */
public class RemapperUtils {

	public static final String NMS_PREFIX = "net/minecraft/server/";
	public static final String NMS_VERSION = Magma.getBukkitVersion();
	private static final Map<String, Boolean> classNeedRemap = new ConcurrentHashMap<>();

	public static String reverseMapExternal(Class<?> name) {
		return reverseMap(name).replace('$', '.').replace('/', '.');
	}

	public static String reverseMap(Class<?> name) {
		return reverseMap(Type.getInternalName(name));
	}

	public static String reverseMap(String check) {
		return ReflectionTransformer.classReverseMapping.getOrDefault(check, check);
	}

	public static String mapMethod(Class<?> inst, String name, Class<?>... parameterTypes) {
		String result = mapMethodInternal(inst, name, parameterTypes);
		if (result != null) {
			return result;
		}

		return name;
	}

	/**
	 * Internal use only
	 */
	@Deprecated
	public static String mapMethodInternal(Class<?> inst, String name, Class<?>... parameterTypes) {
		String match = reverseMap(inst) + "/" + name;

		Collection<String> collection = ReflectionTransformer.methodFastMapping.get(match);
		for (String value : collection) {
			String[] str = value.split("\\s+");
			int i = 0;
			for (Type type : Type.getArgumentTypes(str[1])) {
				String typename = (type.getSort() == Type.ARRAY ? type.getInternalName() : type.getClassName());
				if (i >= parameterTypes.length || !typename.equals(reverseMapExternal(parameterTypes[i]))) {
					i = -1;
					break;
				}
				i++;
			}

			if (i >= parameterTypes.length) {
				return ReflectionTransformer.jarMapping.methods.get(value);
			}
		}

		Class<?> superClass = inst.getSuperclass();
		if (superClass != null) {
			String superMethodName = mapMethodInternal(superClass, name, parameterTypes);
			if (superMethodName != null) {
				return superMethodName;
			}
		}

		for (Class<?> interfaceClass : inst.getInterfaces()) {
			String superMethodName = mapMethodInternal(interfaceClass, name, parameterTypes);
			if (superMethodName != null) return superMethodName;
		}

		return null;
	}

	public static String mapFieldName(Class<?> refc, String name) {
		String key = reverseMap(refc) + "/" + name;
		String mapped = ReflectionTransformer.jarMapping.fields.get(key);
		if (mapped == null) {
			Class<?> superClass = refc.getSuperclass();
			if (superClass != null) {
				mapped = mapFieldName(superClass, name);
			}
		}
		return mapped != null ? mapped : name;
	}

	public static String fixPackageAndMapClass(String className) {
		String remapped = JarRemapper.mapTypeName(className, ReflectionTransformer.jarMapping.packages, ReflectionTransformer.jarMapping.classes, className);
		if (remapped.equals(className) && className.startsWith(NMS_PREFIX) && !className.contains(NMS_VERSION)) {
			String[] splitStr = className.split("/");
			return JarRemapper.mapTypeName(NMS_PREFIX + NMS_VERSION + "/" + splitStr[splitStr.length - 1], ReflectionTransformer.jarMapping.packages, ReflectionTransformer.jarMapping.classes, className);
		}
		return remapped;
	}

	public static String reverseFiled(Field field) {
		String name = field.getName();
		String match = reverseMap(field.getDeclaringClass()) + "/";

		Collection<String> colls = ReflectionTransformer.fieldReverseMapping.get(name);

		for (String value : colls) {
			if (value.startsWith(match)) {
				String[] matched = value.split("\\/");
				String rtr = matched[matched.length - 1];
				return rtr;
			}
		}

		return name;
	}

	public static String reverseMethodName(Method method) {
		String name = method.getName();
		String match = reverseMap(method.getDeclaringClass()) + "/";

		Collection<String> colls = ReflectionTransformer.methodReverseMapping.get(name);

		for (String value : colls) {
			if (value.startsWith(match)) {
				String[] matched = value.split("\\s+")[0].split("\\/");
				String rtr = matched[matched.length - 1];
				return rtr;
			}
		}

		return name;
	}

	public static boolean isNeedRemapClass(Class<?> clazz, boolean checkSuperClass) {
		final String className = clazz.getName();
		Boolean cache = classNeedRemap.get(className);
		if (cache != null) return cache;

		while (clazz != null && clazz.getClassLoader() != null) {
			if (clazz.getName().startsWith("net.minecraft.")) {
				classNeedRemap.put(className, true);
				return true;
			}
			if (checkSuperClass) {
				for (Class<?> interfaceClass : clazz.getInterfaces()) {
					if (isNeedRemapClass(interfaceClass, true)) {
						return true;
					}
				}
				clazz = clazz.getSuperclass();
			} else {
				return false;
			}
		}
		classNeedRemap.put(className, false);
		return false;
	}
}
