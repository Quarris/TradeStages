package dev.quarris.tradestages.mixins;

import dev.quarris.tradestages.trades.IStagedOffer;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
@Mixin(AbstractVillagerEntity.class)
public class AbstractVillagerEntityMixin {

    @Inject(method = "addOffersFromItemListings",
        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/MerchantOffers;add(Ljava/lang/Object;)Z"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void addTradeLevelToOffer(MerchantOffers offers, VillagerTrades.ITrade[] listings, int amountToAdd, CallbackInfo ci, Set<Integer> tradeIds, Iterator<Integer> tradeIdsIte, Integer tradeId, VillagerTrades.ITrade listing, MerchantOffer offer) {
        if (!(offer instanceof IStagedOffer)) {
            return;
        }

        IStagedOffer stagedOffer = (IStagedOffer) offer;

        if ((Object) this instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) (Object) this;
            stagedOffer.setTradeLevel(villager.getVillagerData().getLevel());
            stagedOffer.setProfessionId(ForgeRegistries.PROFESSIONS.getKey(villager.getVillagerData().getProfession()));
        }

        if ((Object) this instanceof WanderingTraderEntity) {
            stagedOffer.setTradeLevel(1);
            stagedOffer.setProfessionId(new ResourceLocation("wandering_trader"));
        }


    }

}
