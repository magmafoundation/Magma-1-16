/*
 * Magma Server
 * Copyright (C) 2019-2021.
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

package org.magmafoundation.magma.remapper.v2.util;

/**
 * ArrayHandle
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 13/07/2021 - 09:40 pm
 */
public class ArrayHandle {

	public String arrayStart = "";
	public String arrayEnd = "";
	public String originClassName = "";

	public ArrayHandle(String className) {
		boolean start = true;
		for (char c : className.toCharArray()) {
			if (start) {
				if (c == '[') {
					arrayStart += '[';
				} else if (c == 'L') {
					arrayStart += 'L';
					start = false;
				}
			} else {
				if (c == ';') {
					if (!arrayEnd.isEmpty()) throw new IllegalArgumentException();
					arrayEnd += ";";
				} else {
					if (!arrayEnd.isEmpty()) throw new IllegalArgumentException();
					originClassName += c;
				}
			}
		}

		if (arrayStart.isEmpty()) throw new IllegalArgumentException();
		if (arrayEnd.isEmpty()) throw new IllegalArgumentException();
		if (originClassName.isEmpty()) throw new IllegalArgumentException();
	}

	public static boolean isArray(String className) {
		return className.startsWith("[L") && className.endsWith(";");
	}
}
