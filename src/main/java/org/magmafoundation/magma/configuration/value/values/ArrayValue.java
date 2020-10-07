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
import java.util.HashSet;
import org.magmafoundation.magma.configuration.ConfigBase;
import org.magmafoundation.magma.configuration.value.Value;

/**
 * ArrayValue
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 19/08/2019 - 05:22 am
 */
public abstract class ArrayValue<T> extends Value<String> {

    protected ConfigBase config;
    protected String values;
    protected HashSet<T> valueSet;
    protected ArrayList<T> valueArray;

    public ArrayValue(ConfigBase config, String path, String key, String description) {
        super(path, key, description);
        this.values = key;
        this.config = config;
        this.initArray(key);
    }

    @Override
    public String getValues() {
        return this.values;
    }

    @Override
    public void setValues(String values) {
        this.values = values;
        this.valueSet.clear();
        this.valueArray.clear();
        this.initArray(values);
        this.config.set(path, valueArray);
    }

    public boolean contains(T t) {
        return this.valueSet.contains(t);
    }

    public T get(int i) {
        if (i < 0 || i > this.valueArray.size() - 1) {
            return null;
        }

        return this.valueArray.get(i);

    }

    public abstract void initArray(String array);
}
