package dev.quarris.tradestages.mixins;

import dev.quarris.tradestages.trades.IStagedOffer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MerchantOffers.class)
public class MerchantOffersMixin {

    @Inject(method = "lambda$writeToStream$0", at = @At("TAIL"))
    private static void writeTradeLevelToStream(FriendlyByteBuf buf, MerchantOffer offer, CallbackInfo ci) {
        if (offer instanceof IStagedOffer stagedOffer) {
            buf.writeVarInt(stagedOffer.getTradeLevel());
            buf.writeResourceLocation(stagedOffer.getProfessionId());
        }
    }

    @Inject(method = "lambda$createFromStream$1", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void writeTradeLevelToStream(FriendlyByteBuf buf, CallbackInfoReturnable<MerchantOffer> cir, ItemStack $$1, ItemStack $$2, ItemStack $$3, boolean $$4, int $$5, int $$6, int $$7, int $$8, float $$9, int $$10, MerchantOffer offer) {
        if (offer instanceof IStagedOffer stagedOffer) {
            stagedOffer.setTradeLevel(buf.readVarInt());
            stagedOffer.setProfessionId(buf.readResourceLocation());
        }
    }
}
