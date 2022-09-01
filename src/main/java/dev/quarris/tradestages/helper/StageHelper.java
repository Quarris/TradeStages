package dev.quarris.tradestages.helper;

import dev.quarris.tradestages.ModRoot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;

public class StageHelper {

    public static boolean canTrade(Player player, MerchantOffer offer) {
        return ModRoot.stagedTrades.canTrade(player, offer);
    }

}
