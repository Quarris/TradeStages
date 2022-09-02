package dev.quarris.tradestages.mixins;

import dev.quarris.tradestages.trades.IStagedOffer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin extends AbstractVillager {

    public WanderingTraderMixin(EntityType<? extends AbstractVillager> p_35267_, Level p_35268_) {
        super(p_35267_, p_35268_);
    }

    @Inject(method = "updateTrades", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/MerchantOffers;add(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void addTradeLevelToRareOffer(CallbackInfo ci, VillagerTrades.ItemListing[] avillagertrades$itemlisting, VillagerTrades.ItemListing[] avillagertrades$itemlisting1, MerchantOffers merchantoffers, int i, VillagerTrades.ItemListing villagertrades$itemlisting, MerchantOffer merchantoffer) {
        if (merchantoffer instanceof IStagedOffer tradeLevelOffer) {
            tradeLevelOffer.setTradeLevel(2);
            tradeLevelOffer.setProfessionId(new ResourceLocation("wandering_trader"));
        }
    }



}
