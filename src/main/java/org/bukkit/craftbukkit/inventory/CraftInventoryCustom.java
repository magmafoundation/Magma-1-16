package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

public class CraftInventoryCustom extends CraftInventory {
    public CraftInventoryCustom(InventoryHolder owner, InventoryType type) {
        super(new MinecraftInventory(owner, type));
    }

    public CraftInventoryCustom(InventoryHolder owner, InventoryType type, String title) {
        super(new MinecraftInventory(owner, type, title));
    }

    public CraftInventoryCustom(InventoryHolder owner, int size) {
        super(new MinecraftInventory(owner, size));
    }

    public CraftInventoryCustom(InventoryHolder owner, int size, String title) {
        super(new MinecraftInventory(owner, size, title));
    }

    // Magma start - add forge inventory support
    public CraftInventoryCustom(InventoryHolder owner, NonNullList<ItemStack> items) {
        super(new MinecraftInventory(owner, items));
    }
    // Magma end

    static class MinecraftInventory implements IInventory {
        private final NonNullList<ItemStack> items;
        private int maxStack = MAX_STACK;
        private final List<HumanEntity> viewers;
        private final String title;
        private InventoryType type;
        private final InventoryHolder owner;

        // Magma start - add forge inventory support
        public MinecraftInventory(InventoryHolder owner, NonNullList<ItemStack> items) {
            this.items = items;
            this.title = "Chest";
            this.viewers = new ArrayList<>();
            this.owner = owner;
            this.type = InventoryType.CHEST;
        }
        // Magma end

        public MinecraftInventory(InventoryHolder owner, InventoryType type) {
            this(owner, type.getDefaultSize(), type.getDefaultTitle());
            this.type = type;
        }

        public MinecraftInventory(InventoryHolder owner, InventoryType type, String title) {
            this(owner, type.getDefaultSize(), title);
            this.type = type;
        }

        public MinecraftInventory(InventoryHolder owner, int size) {
            this(owner, size, "Chest");
        }

        public MinecraftInventory(InventoryHolder owner, int size, String title) {
            Validate.notNull(title, "Title cannot be null");
            this.items = NonNullList.withSize(size, ItemStack.EMPTY);
            this.title = title;
            this.viewers = new ArrayList<HumanEntity>();
            this.owner = owner;
            this.type = InventoryType.CHEST;
        }


        @Override
        public void clearContent() {
            items.clear();
        }

        @Override
        public int getContainerSize() {
            return items.size();
        }

        @Override
        public boolean isEmpty() {
            Iterator iterator = this.items.iterator();
            ItemStack itemstack;
            do {
                if (!iterator.hasNext()) {
                    return true;
                }
                itemstack = (ItemStack) iterator.next();
            } while (itemstack.isEmpty());
            return false;
        }

        @Override
        public ItemStack getItem(int p_70301_1_) {
            return items.get(p_70301_1_);
        }

        @Override
        public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
            ItemStack stack = this.getItem(p_70298_1_);
            ItemStack result;
            if (stack == ItemStack.EMPTY) return stack;
            if (stack.getCount() <= p_70298_2_) {
                this.setItem(p_70298_1_, ItemStack.EMPTY);
                result = stack;
            } else {
                result = CraftItemStack.copyNMSStack(stack, p_70298_2_);
                stack.shrink(p_70298_2_);
            }
            this.setChanged();
            return result;
        }

        @Override
        public ItemStack removeItemNoUpdate(int p_70304_1_) {
            ItemStack stack = this.getItem(p_70304_1_);
            ItemStack result;
            if (stack == ItemStack.EMPTY) return stack;
            if (stack.getCount() <= 1) {
                this.setItem(p_70304_1_, null);
                result = stack;
            } else {
                result = CraftItemStack.copyNMSStack(stack, 1);
                stack.shrink(1);
            }
            return result;
        }

        @Override
        public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
            items.set(p_70299_1_, p_70299_2_);
            if (p_70299_2_ != ItemStack.EMPTY && this.getMaxStackSize() > 0 && p_70299_2_.getCount() > this.getMaxStackSize()) {
                p_70299_2_.setCount(this.getMaxStackSize());
            }
        }

        @Override
        public int getMaxStackSize() {
            return maxStack;
        }

        @Override
        public void setChanged() {

        }

        @Override
        public boolean stillValid(PlayerEntity p_70300_1_) {
            return true;
        }

        @Override
        public List<ItemStack> getContents() {
            return items;
        }

        @Override
        public void onOpen(CraftHumanEntity who) {
            viewers.add(who);
        }

        @Override
        public void onClose(CraftHumanEntity who) {
            viewers.remove(who);
        }

        @Override
        public List<HumanEntity> getViewers() {
            return viewers;
        }

        @Override
        public InventoryHolder getOwner() {
            return owner;
        }

        @Override
        public void setMaxStackSize(int size) {
            maxStack = size;
        }

        @Override
        public Location getLocation() {
            return null;
        }

        public String getTitle() {
            return title;
        }

        public InventoryType getType() {
            return type;
        }
    }
}
