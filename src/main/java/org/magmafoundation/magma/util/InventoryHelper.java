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

package org.magmafoundation.magma.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.block.CraftBlockEntityState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.magmafoundation.magma.craftbukkit.inventory.CraftCustomInventory;

/**
 * InventoryHelper
 *
 * Moved from TileEntity to check for npe
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 07/10/2020 - 08:31 am
 */
public class InventoryHelper {

    /**
     * Get a {@link InventoryHolder}
     * @param tileEntity The Tile entity
     * @return {@link InventoryHolder} or {@link CraftCustomInventory}
     */
    public static InventoryHolder getHolder(TileEntity tileEntity) {
        return getHolder(tileEntity.getLevel(), tileEntity.getBlockPos());
    }

    /**
     * Get a {@link InventoryHolder}
     *
     * @param world The server world.
     * @param pos The tile entity block position
     * @return {@link InventoryHolder} or {@link CraftCustomInventory}
     */
    public static InventoryHolder getHolder(World world, BlockPos pos) {
        if (world == null) {
            return null;
        }
        // Spigot start
        org.bukkit.block.Block block = world.getWorldCB().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        if (block == null) {
            org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.WARNING, "No block for owner at %s %d %d %d", new Object[]{world.getWorldCB(), pos.getX(), pos.getY(), pos.getZ()});
            return null;
        }
        // Spigot end
        org.bukkit.block.BlockState state = block.getState();
        if (state instanceof InventoryHolder) {
            return (InventoryHolder) state;
            // Magma start - add forge inventory support
        } else if (state instanceof CraftBlockEntityState) {
            TileEntity tileEntity = ((CraftBlockEntityState) state).getTileEntity();
            if (tileEntity instanceof Inventory) {
                return new CraftCustomInventory((IInventory) tileEntity);
            }
        }
        // Magma end
        return null;
    }

    /**
     * Get inventory owner
     *
     * @param inventory Tile entity inventory
     * @return owner
     */
    public static InventoryHolder getHolderOwner(IInventory inventory) {
        try {
            return inventory.getOwner();
        } catch (AbstractMethodError error) {
            return (inventory instanceof TileEntity) ? getHolder((TileEntity) inventory) : null;
        }
    }

}
