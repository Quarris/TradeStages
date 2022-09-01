package dev.quarris.tradestages.mixins;

import dev.quarris.tradestages.trades.IStagedOffer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
@Mixin(AbstractVillager.class)
public class AbstractVillagerMixin {

    @Inject(method = "addOffersFromItemListings",
        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/trading/MerchantOffers;add(Ljava/lang/Object;)Z"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void addTradeLevelToOffer(MerchantOffers offers, VillagerTrades.ItemListing[] listings, int amountToAdd, CallbackInfo ci, Set<Integer> tradeIds, Iterator<Integer> tradeIdsIte, Integer tradeId, VillagerTrades.ItemListing listing, MerchantOffer offer) {
        if (!(offer instanceof IStagedOffer tradeLevelOffer)) {
            return;
        }

        if ((Object) this instanceof Villager villager) {
            tradeLevelOffer.setTradeLevel(villager.getVillagerData().getLevel());
            tradeLevelOffer.setProfessionId(ForgeRegistries.VILLAGER_PROFESSIONS.getKey(villager.getVillagerData().getProfession()));
        }

        if ((Object) this instanceof WanderingTrader) {
            tradeLevelOffer.setTradeLevel(1);
            tradeLevelOffer.setProfessionId(new ResourceLocation("wandering_trader"));
        }


    }

}
