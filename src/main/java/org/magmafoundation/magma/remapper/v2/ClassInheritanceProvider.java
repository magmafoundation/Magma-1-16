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

package org.magmafoundation.magma.remapper.v2;

import java.util.Collection;
import java.util.HashSet;

import net.md_5.specialsource.provider.InheritanceProvider;
import org.magmafoundation.magma.remapper.v2.util.RemapperUtils;

/**
 * ClassInheritanceProvider
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 13/07/2021 - 08:43 pm
 */
public class ClassInheritanceProvider implements InheritanceProvider {
	@Override
	public Collection<String> getParents(String className) {
		className = ReflectionTransformer.remapper.map(className);

		try {
			Collection<String> parents = new HashSet<String>();
			Class<?> reference = Class.forName(className.replace('/', '.'), false, this.getClass().getClassLoader());
			Class<?> extend = reference.getSuperclass();
			if (extend != null) {
				parents.add(RemapperUtils.reverseMap(extend));
			}

			for (Class<?> inter : reference.getInterfaces()) {
				if (inter != null) {
					parents.add(RemapperUtils.reverseMap(inter));
				}
			}

			return parents;
		} catch (Exception e) {

		}
		return null;
	}
}
