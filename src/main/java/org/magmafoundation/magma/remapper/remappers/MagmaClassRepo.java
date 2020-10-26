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

import java.io.IOException;
import java.io.InputStream;
import net.md_5.specialsource.repo.CachingRepo;
import org.magmafoundation.magma.remapper.ClassLoaderContext;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

/**
 * MagmaClassRepo
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 11/11/2019 - 08:39 am
 */
public class MagmaClassRepo extends CachingRepo {

    private static final MagmaClassRepo INSTANCE = new MagmaClassRepo();

    public static MagmaClassRepo getInstance() {
        return INSTANCE;
    }

    @Override
    protected ClassNode findClass0(String internalName) {
        InputStream in = getClassLoder().getResourceAsStream(internalName + ".class");
        if (in == null) {
            return null;
        }
        ClassNode classNode = new ClassNode();
        try {
            new ClassReader(in).accept(classNode, 0);
        } catch (IOException e) {
            return null;
        }
        return classNode;
    }

    protected ClassLoader getClassLoder() {
        ClassLoader cl = ClassLoaderContext.peek();
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }
        if (cl == null) {
            cl = this.getClass().getClassLoader();
        }
        return cl;
    }
}