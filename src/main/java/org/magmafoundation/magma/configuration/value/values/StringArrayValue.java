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

package org.magmafoundation.magma.configuration.value.values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.magmafoundation.magma.configuration.ConfigBase;

/**
 * StringArrayValue
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 19/08/2019 - 05:35 am
 */
public class StringArrayValue extends ArrayValue<String> {

    public StringArrayValue(ConfigBase config, String path, String key, String description) {
        super(config, path, key, description);
    }

    @Override
    public void initArray(String array) {
        array = array.replaceAll("\\[(.*)\\]", "$1");
        String[] vals = array.split(",");

        this.valueArray = new ArrayList<String>(vals.length);
        this.valueSet = new HashSet<String>(vals.length);

        Arrays.stream(vals).filter(val -> val.length() != 0).map(String::trim).forEach(val -> this.valueArray.add(val));
        this.valueSet.addAll(this.valueArray);
    }
}
