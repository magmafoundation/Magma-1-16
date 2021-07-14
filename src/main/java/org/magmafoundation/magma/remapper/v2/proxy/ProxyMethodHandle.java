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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.magmafoundation.magma.remapper.v2.MappingLoader;
import org.magmafoundation.magma.remapper.v2.ReflectionMapping;
import org.magmafoundation.magma.remapper.v2.util.RemapperUtils;

/**
 * ProxyMethodHandle
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 13/07/2021 - 08:33 pm
 */
public class ProxyMethodHandle {
    private static final HashMap<String, String> map = new HashMap<>();

    static {
        try {
            ProxyMethodHandle.loadMappings(new BufferedReader(new InputStreamReader(MappingLoader.class.getClassLoader().getResourceAsStream("mappings/nms.srg"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MethodHandle findStatic(MethodHandles.Lookup lookup, Class<?> refc, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
        if (refc.getName().startsWith("net.minecraft.")) {
            name = RemapperUtils.mapMethod(refc, name, type.parameterArray());
        } else {
            Class<?> remappedClass = ReflectionMapping.getStaticMethodTarget((refc.getName().replace(".", "/") + ";" + name));
            if (remappedClass != null) {
                refc = remappedClass;
            }
        }
        return lookup.findStatic(refc, name, type);
    }

    public static MethodHandle findVirtual(MethodHandles.Lookup lookup, Class<?> refc, String name, MethodType oldType) throws NoSuchMethodException, IllegalAccessException {
        if (refc.getName().startsWith("net.minecraft.")) {
            name = RemapperUtils.mapMethod(refc, name, oldType.parameterArray());
        } else {
            Class<?> remappedClass = ReflectionMapping.getVirtualMethodToStaticTarget((refc.getName().replace(".", "/") + ";" + name));
            if (remappedClass != null) {
                Class<?>[] newParArr = new Class<?>[oldType.parameterArray().length + 1];
                newParArr[0] = refc;
                System.arraycopy(oldType.parameterArray(), 0, newParArr, 1, oldType.parameterArray().length);

                MethodType newType = MethodType.methodType(oldType.returnType(), newParArr);
                MethodHandle handle = lookup.findStatic(remappedClass, name, newType);

                return handle;
            }
        }
        return lookup.findVirtual(refc, name, oldType);
    }

    public static MethodHandle findSpecial(MethodHandles.Lookup lookup, Class<?> refc, String name, MethodType type, Class<?> specialCaller) throws NoSuchMethodException, IllegalAccessException {
        if (refc.getName().startsWith("net.minecraft.")) {
            name = RemapperUtils.mapMethod(refc, name, type.parameterArray());
        }
        return lookup.findSpecial(refc, name, type, specialCaller);
    }

    public static MethodHandle findGetter(MethodHandles.Lookup lookup, Class<?> refc, String name, Class<?> type) throws NoSuchFieldException, IllegalAccessException {
        if (refc.getName().startsWith("net.minecraft.")) {
            name = RemapperUtils.mapFieldName(refc, name);
        }
        return lookup.findGetter(refc, name, type);
    }

    public static MethodHandle findSetter(MethodHandles.Lookup lookup, Class<?> refc, String name, Class<?> type) throws NoSuchFieldException, IllegalAccessException {
        if (refc.getName().startsWith("net.minecraft.")) {
            name = RemapperUtils.mapFieldName(refc, name);
        }
        return lookup.findSetter(refc, name, type);
    }

    public static MethodHandle findStaticGetter(MethodHandles.Lookup lookup, Class<?> refc, String name, Class<?> type) throws NoSuchFieldException, IllegalAccessException {
        if (refc.getName().startsWith("net.minecraft.")) {
            name = RemapperUtils.mapFieldName(refc, name);
        }
        return lookup.findStaticGetter(refc, name, type);
    }

    public static MethodHandle findStaticSetter(MethodHandles.Lookup lookup, Class<?> refc, String name, Class<?> type) throws NoSuchFieldException, IllegalAccessException {
        if (refc.getName().startsWith("net.minecraft.")) {
            name = RemapperUtils.mapFieldName(refc, name);
        }
        return lookup.findStaticSetter(refc, name, type);
    }

    public static MethodType fromMethodDescriptorString(String descriptor, ClassLoader loader) {
        String remapDesc = map.getOrDefault(descriptor, descriptor);
        return MethodType.fromMethodDescriptorString(remapDesc, loader);
    }

    public static MethodHandle unreflect(MethodHandles.Lookup lookup, Method m) throws IllegalAccessException {
        Class<?> remappedClass = ReflectionMapping.getVirtualMethodToStaticTarget((m.getDeclaringClass().getName().replace(".", "/") + ";" + m.getName()));
        if (remappedClass != null) {
            try {
                return lookup.unreflect(getClassReflectionMethod(lookup, remappedClass, m));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return lookup.unreflect(m);
    }

    private static Method getClassReflectionMethod(MethodHandles.Lookup lookup, Class<?> remappedClass, Method originalMethod) throws NoSuchMethodException {
        Class<?>[] oldParArr = originalMethod.getParameterTypes();
        Class<?>[] newParArr = new Class<?>[oldParArr.length + 1];
        newParArr[0] = originalMethod.getDeclaringClass();
        System.arraycopy(oldParArr, 0, newParArr, 1, oldParArr.length);

        return remappedClass.getMethod(originalMethod.getName(), newParArr);
    }

    private static void loadMappings(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            int commentIndex = line.indexOf('#');
            if (commentIndex != -1) {
                line = line.substring(0, commentIndex);
            }
            if (line.isEmpty() || !line.startsWith("MD: ")) {
                continue;
            }
            String[] sp = line.split("\\s+");
            String firDesc = sp[2];
            String secDesc = sp[4];
            map.put(firDesc, secDesc);
        }
    }
}
