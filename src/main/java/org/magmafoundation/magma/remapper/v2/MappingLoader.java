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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.transformer.MavenShade;
import org.magmafoundation.magma.Magma;

/**
 * MappingLoader
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 13/07/2021 - 07:30 pm
 */
public class MappingLoader {

	private static final JarMapping jarMappingGlobal = new JarMapping();

	private static final String gloablBukkitLibPath = "org/bukkit/craftbukkit/libs/";

	private static Field fieldPackages;
	private static Field fieldClasses;
	private static Field fieldFields;
	private static Field fieldMethods;

	static {
		initMappings();
	}

	private static void initMappings() {

		try {
			// Put libs for remapping
			jarMappingGlobal.packages.put(gloablBukkitLibPath + "/com/google/gson", "com/google/gson");
			jarMappingGlobal.packages.put(gloablBukkitLibPath + "/libs/it/unimi/dsi/fastutil", "it/unimi/dsi/fastutil");
			jarMappingGlobal.packages.put(gloablBukkitLibPath + "/libs/jline", "jline");
			jarMappingGlobal.packages.put(gloablBukkitLibPath + "/libs/joptsimple", "joptsimple");
			jarMappingGlobal.packages.put(gloablBukkitLibPath + "/libs/org/apache", "org/apache");
			jarMappingGlobal.packages.put(gloablBukkitLibPath + "/libs/org/objectweb/asm", "org/objectweb/asm");

			// Load Mappings
			Map<String, String> relocations = new HashMap<>();
			relocations.put("net.minecraft.server", "net.minecraft.server" + Magma.getBukkitVersion());
			jarMappingGlobal.loadMappings(new BufferedReader(new InputStreamReader(Magma.class.getClassLoader().getResourceAsStream("mappings/nms.srg"))), new MavenShade(relocations), null, false);

			fieldPackages = JarMapping.class.getDeclaredField("packages");
			fieldPackages.setAccessible(true);
			modifyFiledFinal(fieldPackages);

			fieldClasses = JarMapping.class.getDeclaredField("classes");
			fieldClasses.setAccessible(true);
			modifyFiledFinal(fieldClasses);

			fieldFields = JarMapping.class.getDeclaredField("fields");
			fieldFields.setAccessible(true);
			modifyFiledFinal(fieldFields);

			fieldMethods = JarMapping.class.getDeclaredField("methods");
			fieldMethods.setAccessible(true);
			modifyFiledFinal(fieldMethods);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static JarMapping loadMapping() {
		JarMapping jarMapping = new JarMapping();
		try {
			fieldPackages.set(jarMapping, fieldPackages.get(jarMappingGlobal));
			fieldClasses.set(jarMapping, fieldClasses.get(jarMappingGlobal));
			fieldFields.set(jarMapping, fieldFields.get(jarMappingGlobal));
			fieldMethods.set(jarMapping, fieldMethods.get(jarMappingGlobal));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jarMapping;
	}


	public static void modifyFiledFinal(Field field) throws ReflectiveOperationException {
		Field fieldModifiers = Field.class.getDeclaredField("modifiers");
		fieldModifiers.setAccessible(true);
		fieldModifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	}

}
