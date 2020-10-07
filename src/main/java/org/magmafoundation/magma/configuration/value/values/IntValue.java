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

import org.apache.commons.lang.math.NumberUtils;
import org.magmafoundation.magma.configuration.ConfigBase;
import org.magmafoundation.magma.configuration.value.Value;

/**
 * IntValue
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 19/08/2019 - 05:34 am
 */
public class IntValue extends Value<Integer> {

    private Integer value;
    private ConfigBase configBase;

    public IntValue(ConfigBase configBase, String path, Integer key, String description) {
        super(path, key, description);
        this.value = key;
        this.configBase = configBase;
    }

    @Override
    public Integer getValues() {
        return this.value;
    }

    @Override
    public void setValues(String values) {
        this.value = NumberUtils.toInt(values, key);
        configBase.set(path, this.value);
    }
}
