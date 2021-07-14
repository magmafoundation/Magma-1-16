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
