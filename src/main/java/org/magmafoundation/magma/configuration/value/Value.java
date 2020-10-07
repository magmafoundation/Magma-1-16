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

package org.magmafoundation.magma.configuration.value;

/**
 * Value
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 19/08/2019 - 05:19 am
 */
public abstract class Value<T> {

    public final String path;
    public final T key;
    public final String description;

    public Value(String path, T key, String description) {
        this.path = path;
        this.key = key;
        this.description = description;
    }

    public abstract T getValues();

    public abstract void setValues(String values);
}
