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

package org.magmafoundation.magma.forge;

import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Material;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.EntityType;
import org.magmafoundation.magma.craftbukkit.entity.CraftCustomEntity;
import org.magmafoundation.magma.util.EnumHelper;

/**
 * ForgeInject
 *
 * This class it made to add Forge Blocks, Items, Entitys, Enchantments, Potions and Banner Patterns
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 08/06/2020 - 06:36 pm
 */
public class ForgeInject {

    public static final Logger LOGGER = LogManager.getLogger();

    public static void injectForge() {
        addForgeItems();
        addForgeBlocks();
        addForgeEntitys();
    }

    private static void addForgeItems() {

        ForgeRegistries.ITEMS.getEntries().forEach(registryKeyItemEntry -> {
            ResourceLocation resourceLocation = registryKeyItemEntry.getValue().getRegistryName();
            Item item = registryKeyItemEntry.getValue();
            if(!resourceLocation.getNamespace().equals("minecraft")) {
                String materialName = Material.normalizeName(registryKeyItemEntry.getKey().toString()).replace("RESOURCEKEYMINECRAFT_ITEM__", "");
                Material material = Material
                    .addMaterial(EnumHelper.addEnum(Material.class, materialName, new Class[]{Integer.TYPE, Integer.TYPE}, new Object[]{Item.getIdFromItem(item), item.getMaxStackSize()}));
                CraftMagicNumbers.ITEM_MATERIAL.put(item, material);
                CraftMagicNumbers.MATERIAL_ITEM.put(material, item);
                if (material != null) {
                    LOGGER.info(String.format("Injected new Forge item material %s.", material.name()));
                } else {
                    LOGGER.info(String.format("Inject item failure %s with ID %d.", materialName, Item.getIdFromItem(item)));
                }
            }
        });
    }

    private static void addForgeBlocks() {
        ForgeRegistries.BLOCKS.getEntries().forEach(registryKeyBlockEntry -> {
            ResourceLocation resourceLocation = registryKeyBlockEntry.getValue().getRegistryName();
            Block block = registryKeyBlockEntry.getValue();
            if(!resourceLocation.getNamespace().equals("minecraft")) {
                String materialName = Material.normalizeName(registryKeyBlockEntry.getKey().toString()).replace("RESOURCEKEYMINECRAFT_BLOCK__", "");
                Material material = Material.addMaterial(EnumHelper.addEnum(Material.class, materialName, new Class[]{Integer.TYPE}, new Object[]{Item.getIdFromItem(block.asItem())}));
                CraftMagicNumbers.BLOCK_MATERIAL.put(block, material);
                CraftMagicNumbers.MATERIAL_BLOCK.put(material, block);
                if (material != null) {
                    LOGGER.info(String.format("Injected new Forge block material %s.", material.name()));
                } else {
                    LOGGER.info(String.format("Inject block failure %s with ID %d.", materialName, Item.getIdFromItem(block.asItem())));
                }
            }
        });
    }

    private static void addForgeEntitys() {
        Map<String, EntityType> NAME_MAP = ObfuscationReflectionHelper.getPrivateValue(EntityType.class, null, "NAME_MAP");
        Map<Short, EntityType> ID_MAP = ObfuscationReflectionHelper.getPrivateValue(EntityType.class, null, "ID_MAP");

        ForgeRegistries.ENTITIES.getEntries().forEach(entity -> {
            if (!entity.getValue().getRegistryName().getNamespace().equals("minecraft")) {
                String entityname = entity.getKey().getRegistryName().getNamespace();
                String entityType = entityname.replace("-", "_").toUpperCase();

                EntityType bukkitType = EnumHelper.addEnum(EntityType.class, entityType, new Class[]{String.class, Class.class, Integer.TYPE, Boolean.TYPE},
                    new Object[]{entityname, CraftCustomEntity.class, entity.getKey().getRegistryName().getNamespace().hashCode(), false});

                NAME_MAP.put(entityname.toLowerCase(), bukkitType);
                ID_MAP.put((short) entity.getKey().getRegistryName().getNamespace().hashCode(), bukkitType);
            }
        });

    }


}
