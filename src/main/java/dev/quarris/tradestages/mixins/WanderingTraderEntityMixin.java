package dev.quarris.tradestages.mixins;

import dev.quarris.tradestages.trades.IStagedOffer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WanderingTraderEntity.class)
public abstract class WanderingTraderEntityMixin extends AbstractVillagerEntity {

    public WanderingTraderEntityMixin(EntityType<? extends AbstractVillagerEntity> p_35267_, World p_35268_) {
        super(p_35267_, p_35268_);
    }

    @Inject(method = "updateTrades", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/MerchantOffers;add(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void addTradeLevelToRareOffer(CallbackInfo ci, VillagerTrades.ITrade[] avillagertrades$itemlisting, VillagerTrades.ITrade[] avillagertrades$itemlisting1, MerchantOffers merchantoffers, int i, VillagerTrades.ITrade villagertrades$itemlisting, MerchantOffer merchantoffer) {
        if (merchantoffer instanceof IStagedOffer) {
            IStagedOffer stagedOffer = (IStagedOffer) merchantoffer;
            stagedOffer.setTradeLevel(2);
            stagedOffer.setProfessionId(new ResourceLocation("wandering_trader"));
        }
    }



}
