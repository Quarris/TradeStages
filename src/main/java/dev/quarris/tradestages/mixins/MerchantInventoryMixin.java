package dev.quarris.tradestages.mixins;

import dev.quarris.tradestages.helper.StageHelper;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.MerchantInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(MerchantInventory.class)
public abstract class MerchantInventoryMixin {

    @Shadow @Nullable private MerchantOffer activeOffer;

    @Shadow @Final private IMerchant merchant;

    @Shadow public abstract void setItem(int p_40013_, ItemStack p_40014_);

    @Shadow public abstract ItemStack getItem(int p_40008_);

    @Shadow private int futureXp;

    @Inject(method = "updateSellItem", at = @At("TAIL"))
    public void removeInvalidOffer(CallbackInfo ci) {
        PlayerEntity player = this.merchant.getTradingPlayer();
        if (player == null) {
            return;
        }

        if (this.activeOffer != null) {
            if (!StageHelper.canTrade(this.merchant.getTradingPlayer(), this.activeOffer)) {
                this.activeOffer = null;
                this.setItem(2, ItemStack.EMPTY);
                this.futureXp = 0;
                this.merchant.notifyTradeUpdated(this.getItem(2));
            }
        }
    }

}
