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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.magmafoundation.magma.Magma;
import org.magmafoundation.magma.remapper.inter.ClassRemapperSupplier;
import org.magmafoundation.magma.remapper.utils.ASMUtils;
import org.objectweb.asm.commons.Remapper;

/**
 * NMSVersionRemapper
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 11/11/2019 - 08:00 am
 */
public class NMSVersionRemapper extends Remapper implements ClassRemapperSupplier {

    private static final String targetVersion = Magma.getBukkitVersion();
    private static final Pattern cbPattern = Pattern.compile("org/bukkit/craftbukkit/(v\\d_\\d+_R\\d)/[\\w/]+");
    private static final Pattern nmsPattern = Pattern.compile("net/minecraft/server/(v\\d_\\d+_R\\d)/[\\w/]+");

    @Override
    public String map(String typeName) {
        String str = typeName;
        Matcher m = cbPattern.matcher(str);
        if (m.find()) {
            String srcVersion = m.group(1);
            if (!Objects.equals(srcVersion, targetVersion)) {
                str = str.replace(srcVersion, targetVersion);
            }
        } else {
            m = nmsPattern.matcher(typeName);
            if (m.find()) {
                String srcVersion = m.group(1);
                if (!Objects.equals(srcVersion, targetVersion)) {
                    str = str.replace(srcVersion, targetVersion);
                }
            }
        }
        return str;
    }

    @Override
    public String mapSignature(String signature, boolean typeSignature) {
        if (ASMUtils.isValidSingnature(signature)) {
            return super.mapSignature(signature, typeSignature);
        } else {
            return signature;
        }
    }
}
