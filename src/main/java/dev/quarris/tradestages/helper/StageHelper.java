package dev.quarris.tradestages.helper;

import dev.quarris.tradestages.ModRoot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MerchantOffer;

public class StageHelper {

    public static boolean canTrade(PlayerEntity player, MerchantOffer offer) {
        return ModRoot.stagedTrades.canTrade(player, offer);
    }

}
