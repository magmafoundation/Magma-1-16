/*
 * Magma Server
 * Copyright (C) 2019-2020.
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

package org.magmafoundation.magma.remapper.proxy;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import org.magmafoundation.magma.remapper.utils.RemappingUtils;

/**
 * ProxyMethodHandles_Lookup
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 11/11/2019 - 08:49 am
 */
public class ProxyMethodHandles_Lookup {

    public static MethodHandle findVirtual(MethodHandles.Lookup lookup, Class<?> clazz, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
        if (clazz.getName().startsWith("net.minecraft.")) {
            name = RemappingUtils.mapMethodName(clazz, name, type);
        } else if (clazz == Class.class) {
            switch (name) {
                case "getField":
                case "getDeclaredField":
                case "getMethod":
                case "getDeclaredMethod":
                    type = MethodType.methodType(type.returnType(), new Class[]{Class.class, String.class});
                    clazz = ProxyClass.class;
                    break;
                default:
            }
        } else if (clazz == ClassLoader.class) {
            if (name.equals("loadClass")) {
                type = MethodType.methodType(type.returnType(), new Class[]{ClassLoader.class, String.class});
                clazz = ProxyClassLoader.class;
            }
        }
        return lookup.findVirtual(clazz, name, type);
    }

    public static MethodHandle findStatic(MethodHandles.Lookup lookup, Class<?> clazz, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
        if (clazz.getName().startsWith("net.minecraft.")) {
            name = RemappingUtils.mapMethodName(clazz, name, type);
        } else if (clazz == Class.class && name.equals("forName")) {
            clazz = ProxyClass.class;
        }
        return lookup.findStatic(clazz, name, type);
    }

    public static MethodHandle findSpecial(MethodHandles.Lookup lookup, Class<?> clazz, String name, MethodType type, Class<?> specialCaller) throws NoSuchMethodException, IllegalAccessException {
        if (clazz.getName().startsWith("net.minecraft.")) {
            name = RemappingUtils.mapMethodName(clazz, name, type);
        }
        return lookup.findSpecial(clazz, name, type, specialCaller);
    }

    public static MethodHandle unreflect(MethodHandles.Lookup lookup, Method m) throws IllegalAccessException, NoSuchMethodException {
        if (m.getDeclaringClass() == Class.class) {
            String name = m.getName();
            switch (name) {
                case "forName":
                    return lookup.unreflect(ProxyClass.class.getMethod(name, new Class[]{String.class}));
                case "getField":
                case "getDeclaredField": {
                    return lookup.unreflect(ProxyClass.class.getMethod(name, new Class[]{Class.class, String.class}));
                }
                case "getMethod":
                case "getDeclaredMethod":
                    return lookup.unreflect(ProxyClass.class.getMethod(name, new Class[]{Class.class, String.class, Class[].class}));
            }
        } else if (m.getDeclaringClass() == ClassLoader.class && m.getName().equals("loadClass")) {
            return lookup.unreflect(ClassLoader.class.getMethod(m.getName(), new Class[]{ClassLoader.class, String.class}));
        }
        return lookup.unreflect(m);
    }

}
