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

package org.magmafoundation.magma.remapper.v2.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.magmafoundation.magma.remapper.v2.ClassLoaderUtil;
import org.magmafoundation.magma.remapper.v2.ReflectionMapping;
import org.magmafoundation.magma.remapper.v2.ReflectionTransformer;
import org.magmafoundation.magma.remapper.v2.util.ArrayHandle;
import org.magmafoundation.magma.remapper.v2.util.RemapperUtils;

/**
 * ProxyReflection
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 13/07/2021 - 08:29 pm
 */
public class ProxyReflection {
	private final static ConcurrentHashMap<Field, String> fieldGetNameCache = new ConcurrentHashMap<>();
	private final static ConcurrentHashMap<Method, String> methodGetNameCache = new ConcurrentHashMap<>();
	private final static ConcurrentHashMap<Class<?>, String> simpleNameGetNameCache = new ConcurrentHashMap<>();

	public static Class<?> forName(String className) throws ClassNotFoundException {
		return forName(className, true, ClassLoaderUtil.getCallerClassLoader());
	}

	public static Class<?> forName(String className, boolean initialize, ClassLoader classLoader) throws ClassNotFoundException {
		if (ArrayHandle.isArray(className)) {
			try {
				ArrayHandle arrayHandle = new ArrayHandle(className);
				if (ReflectionMapping.isNeedRemapClass(arrayHandle.originClassName)) {
					String remapped = ReflectionTransformer.jarMapping.classes.getOrDefault(arrayHandle.originClassName.replace('.', '/'), arrayHandle.originClassName).replace('/', '.');
					className = arrayHandle.arrayStart + remapped + arrayHandle.arrayEnd;
				}
			} catch (IllegalArgumentException e) {
			}
		} else {
			if (ReflectionMapping.isNeedRemapClass(className)) {
				className = ReflectionTransformer.jarMapping.classes.getOrDefault(className.replace('.', '/'), className).replace('/', '.');
			}
		}

		return Class.forName(className, initialize, classLoader);
	}

	public static String getSimpleName(Class<?> inst) {
		if (!RemapperUtils.isNeedRemapClass(inst, false)) return inst.getSimpleName();
		String cache = simpleNameGetNameCache.get(inst);
		if (cache != null) return cache;
		String[] name = RemapperUtils.reverseMapExternal(inst).split("\\.");
		String retn = name[name.length - 1];
		simpleNameGetNameCache.put(inst, retn);
		return retn;
	}

	public static String getName(Class<?> inst) {
		if (!RemapperUtils.isNeedRemapClass(inst, true)) return inst.getName();
		return RemapperUtils.reverseMap(inst).replace("/", ".");
	}

	public static Field getDeclaredField(Class<?> inst, String name) throws NoSuchFieldException, SecurityException {
		if (RemapperUtils.isNeedRemapClass(inst, false)) {
			name = ReflectionTransformer.remapper.mapFieldName(RemapperUtils.reverseMap(inst), name, null);
		}
		return inst.getDeclaredField(name);
	}

	public static Method getDeclaredMethod(Class<?> inst, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		if (RemapperUtils.isNeedRemapClass(inst, true)) {
			name = RemapperUtils.mapMethod(inst, name, parameterTypes);
		}
		try {
			return inst.getDeclaredMethod(name, parameterTypes);
		} catch (NoClassDefFoundError e) {
			throw new NoSuchMethodException(e.toString());
		}
	}

	public static Method[] getDeclaredMethods(Class<?> inst) {
		try {
			return inst.getDeclaredMethods();
		} catch (NoClassDefFoundError e) {
			return new Method[] {};
		}
	}

	public static Field getField(Class<?> inst, String name) throws NoSuchFieldException, SecurityException {
		if (RemapperUtils.isNeedRemapClass(inst, true)) {
			name = RemapperUtils.mapFieldName(inst, name);
		}
		return inst.getField(name);
	}

	public static Method getMethod(Class<?> inst, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		if (RemapperUtils.isNeedRemapClass(inst, true)) {
			name = RemapperUtils.mapMethod(inst, name, parameterTypes);
		}
		try {
			return inst.getMethod(name, parameterTypes);
		} catch (NoClassDefFoundError e) {
			throw new NoSuchMethodException(e.toString());
		}

	}

	public static String getName(Field field) {
		if (!RemapperUtils.isNeedRemapClass(field.getDeclaringClass(), false)) return field.getName();
		String cache = fieldGetNameCache.get(field);
		if (cache != null) return cache;
		String retn = RemapperUtils.reverseFiled(field);
		fieldGetNameCache.put(field, retn);
		return retn;
	}

	public static String getName(Method method) {
		if (!RemapperUtils.isNeedRemapClass(method.getDeclaringClass(), true)) return method.getName();
		String cache = methodGetNameCache.get(method);
		if (cache != null) return cache;
		String retn = RemapperUtils.reverseMethodName(method);
		methodGetNameCache.put(method, retn);
		return retn;
	}

	public static Class<?> loadClass(ClassLoader inst, String className) throws ClassNotFoundException {
		if (ReflectionMapping.isNeedRemapClass(className) || className.startsWith("net.minecraft.")) {
			className = RemapperUtils.fixPackageAndMapClass(className.replace('.', '/')).replace('/', '.');
		}
		return inst.loadClass(className);
	}
}
