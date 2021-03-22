package org.bukkit.craftbukkit.inventory;

import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.Nullable;

public class CraftMerchantCustom extends CraftMerchant {

    public CraftMerchantCustom(String title) {
        super(new MinecraftMerchant(title));
        getMerchant().craftMerchant = this;
    }

    @Override
    public String toString() {
        return "CraftMerchantCustom";
    }

    @Override
    public MinecraftMerchant getMerchant() {
        return (MinecraftMerchant) super.getMerchant();
    }

    public static class MinecraftMerchant implements IMerchant {

        private final ITextComponent title;
        private final MerchantOffers trades = new MerchantOffers();
        private PlayerEntity tradingPlayer;
        private World tradingWorld;
        protected CraftMerchant craftMerchant;

        public MinecraftMerchant(String title) {
            Validate.notNull(title, "Title cannot be null");
            this.title = new StringTextComponent(title);
        }

        @Override
        public void setTradingPlayer(@Nullable PlayerEntity p_70932_1_) {
            this.tradingPlayer = p_70932_1_;
            if(p_70932_1_ != null){
                this.tradingWorld = p_70932_1_.level;
            }
        }

        @Nullable
        @Override
        public PlayerEntity getTradingPlayer() {
            return this.tradingPlayer;
        }

        @Override
        public MerchantOffers getOffers() {
            return this.trades;
        }

        @Override
        public void overrideOffers(@Nullable MerchantOffers p_213703_1_) {

        }

        @Override
        public void notifyTrade(MerchantOffer p_213704_1_) {
            p_213704_1_.increaseUses();
        }

        @Override
        public void notifyTradeUpdated(ItemStack p_110297_1_) {

        }

        @Override
        public World getLevel() {
            return this.tradingWorld;
        }

        @Override
        public int getVillagerXp() {
            return 0;
        }

        @Override
        public void overrideXp(int p_213702_1_) {

        }

        @Override
        public boolean showProgressBar() {
            return false;  // is-regular-villager flag (hides some gui elements: xp bar, name suffix)
        }

        @Override
        public SoundEvent getNotifyTradeSound() {
            return SoundEvents.VILLAGER_YES;
        }

        @Override
        public CraftMerchant getCraftMerchant() {
            return craftMerchant;
        }

        public ITextComponent getTitle() {
            return title;
        }
    }
}
