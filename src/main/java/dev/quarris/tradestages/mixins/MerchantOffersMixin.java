package dev.quarris.tradestages.mixins;

import dev.quarris.tradestages.trades.IStagedOffer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.network.PacketBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;

@Mixin(MerchantOffers.class)
public class MerchantOffersMixin extends ArrayList<MerchantOffer> {

    @Inject(method = "writeToStream", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/MerchantOffer;getDemand()I", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void writeTradeLevelToStream(PacketBuffer buf, CallbackInfo ci, int i, MerchantOffer offer, ItemStack unused) {
        if (offer instanceof IStagedOffer) {
            IStagedOffer stagedOffer = (IStagedOffer) offer;
            buf.writeVarInt(stagedOffer.getTradeLevel());
            buf.writeResourceLocation(stagedOffer.getProfessionId());
        }
    }

    @Inject(method = "createFromStream", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/MerchantOffers;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void writeTradeLevelToStream(PacketBuffer buf, CallbackInfoReturnable<MerchantOffers> cir, MerchantOffers merchantoffers, int i, int j, ItemStack itemstack, ItemStack itemstack1, ItemStack itemstack2, boolean flag, int k, int l, int i1, int j1, float f, int k1, MerchantOffer offer) {
        if (offer instanceof IStagedOffer) {
            IStagedOffer stagedOffer = (IStagedOffer) offer;
            stagedOffer.setTradeLevel(buf.readVarInt());
            stagedOffer.setProfessionId(buf.readResourceLocation());
        }
    }
}
