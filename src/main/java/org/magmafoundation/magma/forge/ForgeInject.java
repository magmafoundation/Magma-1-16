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

package org.magmafoundation.magma.forge;

import static org.bukkit.Material.normalizeName;

import java.util.Map;
import java.util.stream.IntStream;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_16_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_16_R3.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
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

    public static BiMap<RegistryKey<DimensionType>, World.Environment> environment = HashBiMap.create(ImmutableMap.<RegistryKey<DimensionType>, World.Environment>builder()
            .put(DimensionType.OVERWORLD_LOCATION, World.Environment.NORMAL)
            .put(DimensionType.NETHER_LOCATION, World.Environment.NETHER)
            .put(DimensionType.END_LOCATION, World.Environment.THE_END)
            .build());

    public static final Logger LOGGER = LogManager.getLogger();

    public static void injectForge() {
        addForgeItems();
        addForgeBlocks();
        addForgeEntitys();
        addForgeEnchantments();
        addForgePotions();
        addForgeBannerPatters();
    }

    private static void addForgeItems() {

        ForgeRegistries.ITEMS.getEntries().forEach(registryKeyItemEntry -> {
            ResourceLocation resourceLocation = registryKeyItemEntry.getValue().getRegistryName();
            Item item = registryKeyItemEntry.getValue();
            if(!resourceLocation.getNamespace().equals("minecraft")) {
                String materialName = normalizeName(registryKeyItemEntry.getKey().toString()).replace("RESOURCEKEYMINECRAFT_ITEM__", "");
                Material material = Material
                    .addMaterial(EnumHelper.addEnum(Material.class, materialName, new Class[]{Integer.TYPE, Integer.TYPE}, new Object[]{Item.getId(item), item.getMaxStackSize()}));
                CraftMagicNumbers.ITEM_MATERIAL.put(item, material);
                CraftMagicNumbers.MATERIAL_ITEM.put(material, item);
                if (material != null) {
                    LOGGER.info(String.format("Injected new Forge item material %s.", material.name()));
                } else {
                    LOGGER.info(String.format("Inject item failure %s with ID %d.", materialName, Item.getId(item)));
                }
            }
        });
    }

    private static void addForgeBlocks() {
        ForgeRegistries.BLOCKS.getEntries().forEach(registryKeyBlockEntry -> {
            ResourceLocation resourceLocation = registryKeyBlockEntry.getValue().getRegistryName();
            Block block = registryKeyBlockEntry.getValue();
            if(!resourceLocation.getNamespace().equals("minecraft")) {
                String materialName = normalizeName(registryKeyBlockEntry.getKey().toString()).replace("RESOURCEKEYMINECRAFT_BLOCK__", "");
                Material material = Material.addMaterial(EnumHelper.addEnum(Material.class, materialName, new Class[]{Integer.TYPE}, new Object[]{Item.getId(block.asItem())}));
                CraftMagicNumbers.BLOCK_MATERIAL.put(block, material);
                CraftMagicNumbers.MATERIAL_BLOCK.put(material, block);
                if (material != null) {
                    LOGGER.info(String.format("Injected new Forge block material %s.", material.name()));
                } else {
                    LOGGER.info(String.format("Inject block failure %s with ID %d.", materialName, Item.getId(block.asItem())));
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

    private static void addForgeEnchantments() {
        ForgeRegistries.ENCHANTMENTS.getEntries().forEach(registryKeyEnchantmentEntry -> Enchantment.registerEnchantment(new CraftEnchantment(registryKeyEnchantmentEntry.getValue())));
        Enchantment.stopAcceptingRegistrations();
    }

    private static void addForgePotions() {
        ForgeRegistries.POTIONS.getEntries().forEach(registryKeyEffectEntry -> PotionEffectType.registerPotionEffectType(new CraftPotionEffectType(registryKeyEffectEntry.getValue())));
        PotionEffectType.stopAcceptingRegistrations();
    }

    private static void addForgeBannerPatters() {
        Map<String, PatternType> PATTERN_MAP = ObfuscationReflectionHelper.getPrivateValue(PatternType.class, null, "byString");
        IntStream.range(0, BannerPattern.values().length).forEach(i -> {
            PatternType patternType = EnumHelper.addEnum(PatternType.class, BannerPattern.values()[i].name(), new Class[]{String.class}, new Object[]{BannerPattern.values()[i].getHashname()});
            PATTERN_MAP.put(BannerPattern.values()[i].getHashname(), patternType);
        });

    }

    public static void addForgeEnvironment() {
        int i = World.Environment.values().length;
        Registry<DimensionType> registry = MinecraftServer.getServer().registryHolder.dimensionTypes();
        for (Map.Entry<RegistryKey<DimensionType>, DimensionType> entry : registry.entrySet()) {
            RegistryKey<DimensionType> key = entry.getKey();
            World.Environment environment1 = environment.get(key);
            if (environment1 == null) {
                String name = normalizeName(key.location().toString());
                int id = i - 1;
                environment1 = EnumHelper.addEnum(World.Environment.class, name, new Class[]{Integer.TYPE}, new Object[]{id});
                environment.put(key, environment1);
                LOGGER.info(String.format("Injected new Forge DimensionType %s.", environment1));
                i++;
            }
        }
    }

}
