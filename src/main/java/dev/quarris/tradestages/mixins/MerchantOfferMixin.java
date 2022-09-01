package dev.quarris.tradestages.mixins;

import dev.quarris.tradestages.trades.IStagedOffer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MerchantOffer.class)
public class MerchantOfferMixin implements IStagedOffer {

    private int tradeLevel;
    private ResourceLocation professionId;

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    public void loadTradeLevelFromTag(CompoundTag tag, CallbackInfo ci) {
        this.tradeLevel = tag.getInt("tradeLevel");
        this.professionId = new ResourceLocation(tag.getString("professionId"));
    }

    @Inject(method = "createTag",
        at = @At(value = "TAIL"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void addTradeLevelToTag(CallbackInfoReturnable<CompoundTag> cir, CompoundTag tag) {
        tag.putInt("tradeLevel", this.tradeLevel);
        tag.putString("professionId", this.professionId.toString());
    }

    @Override
    public int getTradeLevel() {
        return this.tradeLevel;
    }

    @Override
    public void setTradeLevel(int level) {
        this.tradeLevel = level;
    }

    @Override
    public ResourceLocation getProfessionId() {
        return this.professionId;
    }

    @Override
    public void setProfessionId(ResourceLocation id) {
        this.professionId = id;
    }
}
