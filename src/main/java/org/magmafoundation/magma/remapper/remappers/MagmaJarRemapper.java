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

package org.magmafoundation.magma.remapper.remappers;

import java.util.Iterator;
import java.util.Map;
import net.md_5.specialsource.CustomRemapper;
import net.md_5.specialsource.NodeType;
import org.magmafoundation.magma.remapper.mappingsModel.ClassMappings;
import org.magmafoundation.magma.remapper.utils.ASMUtils;

/**
 * MagmaJarRemapper
 *
 * @author Hexeption admin@hexeption.co.uk
 * @author md-5
 * @since 11/11/2019 - 08:16 am
 */
public class MagmaJarRemapper extends CustomRemapper {

    public final MagmaJarMapping jarMapping;

    public MagmaJarRemapper(MagmaJarMapping jarMapping) {
        this.jarMapping = jarMapping;
    }

    public static String mapTypeName(String typeName, Map<String, String> packageMap, Map<String, ClassMappings> classMap, String defaultIfUnmapped) {
        String mapped = mapClassName(typeName, packageMap, classMap);
        return mapped != null ? mapped : defaultIfUnmapped;
    }

    /**
     * Helper method to map a class name by package (prefix) or class (exact)
     */
    private static String mapClassName(String className, Map<String, String> packageMap, Map<String, ClassMappings> classMap) {
        if (classMap != null && classMap.containsKey(className)) {
            ClassMappings mapping = classMap.get(className);
            return mapping.getMcpSrcName();
        }

        int index = className.lastIndexOf('$');
        if (index != -1) {
            String outer = className.substring(0, index);
            String mapped = mapClassName(outer, packageMap, classMap);
            if (mapped == null) {
                return null;
            }
            return mapped + className.substring(index);
        }

        if (packageMap != null) {
            Iterator<String> iter = packageMap.keySet().iterator();
            while (iter.hasNext()) {
                String oldPackage = iter.next();
                if (matchClassPackage(oldPackage, className)) {
                    String newPackage = packageMap.get(oldPackage);

                    return moveClassPackage(newPackage, getSimpleName(oldPackage, className));
                }
            }
        }

        return null;
    }

    private static boolean matchClassPackage(String packageName, String className) {
        if (packageName.equals(".")) {
            return isDefaultPackage(className);
        }

        return className.startsWith(packageName);
    }

    private static String moveClassPackage(String packageName, String classSimpleName) {
        if (packageName.equals(".")) {
            return classSimpleName;
        }

        return packageName + classSimpleName;
    }

    private static boolean isDefaultPackage(String className) {
        return className.indexOf('/') == -1;
    }

    private static String getSimpleName(String oldPackage, String className) {
        if (oldPackage.equals(".")) {
            return className;
        }

        return className.substring(oldPackage.length());
    }

    @Override
    public String mapSignature(String signature, boolean typeSignature) {
        if (ASMUtils.isValidSingnature(signature)) {
            return super.mapSignature(signature, typeSignature);
        } else {
            return signature;
        }
    }

    @Override
    public String map(String typeName) {
        return mapTypeName(typeName, jarMapping.packages, jarMapping.byNMSSrcName, typeName);
    }

    @Override
    public String mapFieldName(String owner, String name, String desc, int access) {
        String mapped = jarMapping.tryClimb(jarMapping.fields, NodeType.FIELD, owner, name, access);
        return mapped == null ? name : mapped;
    }

    @Override
    public String mapMethodName(String owner, String name, String desc, int access) {
        String mapped = jarMapping.tryClimb(jarMapping.methods, NodeType.METHOD, owner, name + " " + desc, access);
        return mapped == null ? name : mapped;
    }

}
