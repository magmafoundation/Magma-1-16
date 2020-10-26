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

import org.magmafoundation.magma.remapper.proxy.DelegateURLClassLoder;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

/**
 * MagmaClassRemapper
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 11/11/2019 - 08:44 am
 */
public class MagmaClassRemapper extends ClassRemapper {

    public MagmaClassRemapper(ClassVisitor cv, Remapper remapper) {
        super(cv, remapper);
    }

    protected MagmaClassRemapper(int api, ClassVisitor cv, Remapper remapper) {
        super(api, cv, remapper);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if ("java/net/URLClassLoader".equals(superName)) {
            superName = DelegateURLClassLoder.desc;
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    protected MethodVisitor createMethodRemapper(MethodVisitor mv) {
        return new ReflectionMethodRemapper(mv, remapper);
    }
}
