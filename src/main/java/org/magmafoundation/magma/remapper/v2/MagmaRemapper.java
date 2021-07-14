package org.magmafoundation.magma.remapper.v2;

import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;

/**
 * MagmaRemapper
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 13/07/2021 - 08:00 pm
 */
public class MagmaRemapper extends JarRemapper {

	public MagmaRemapper(JarMapping jarMapping) {
		super(jarMapping);
	}

	public String mapSignature(String signature, boolean typeSignature) {
		try {
			return super.mapSignature(signature, typeSignature);
		} catch (Exception e) {
			return signature;
		}
	}

	@Override
	public String mapFieldName(String owner, String name, String desc, int access) {
		return super.mapFieldName(owner, name, desc, -1);
	}
}
