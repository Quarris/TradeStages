package dev.quarris.tradestages.mixins;

import dev.quarris.tradestages.trades.IStagedOffer;
import net.minecraft.item.MerchantOffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
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

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundNBT;)V", at = @At("TAIL"))
    public void loadTradeLevelFromTag(CompoundNBT nbt, CallbackInfo ci) {
        this.tradeLevel = nbt.getInt("tradeLevel");
        this.professionId = new ResourceLocation(nbt.getString("professionId"));
    }

    @Inject(method = "createTag",
        at = @At(value = "TAIL"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void addTradeLevelToTag(CallbackInfoReturnable<CompoundNBT> cir, CompoundNBT nbt) {
        nbt.putInt("tradeLevel", this.tradeLevel);
        nbt.putString("professionId", this.professionId.toString());
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
