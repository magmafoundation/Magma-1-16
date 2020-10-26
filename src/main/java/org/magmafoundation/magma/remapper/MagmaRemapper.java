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

package org.magmafoundation.magma.remapper;

import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;

public class MagmaRemapper extends JarRemapper {

    public MagmaRemapper(JarMapping jarMapping) {
        super(jarMapping);
    }

    @Override
    public String mapSignature(final String signature, final boolean typeSignature) {
        try {
            return super.mapSignature(signature, typeSignature);
        } catch (Exception e) {
            return signature;
        }
    }

    @Override
    public String mapFieldName(final String owner, final String name, final String desc, final int access) {
        return super.mapFieldName(owner, name, desc, -1);
    }
}
