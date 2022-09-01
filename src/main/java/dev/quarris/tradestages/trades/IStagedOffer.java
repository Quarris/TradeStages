package dev.quarris.tradestages.trades;

import net.minecraft.util.ResourceLocation;

public interface IStagedOffer {

    int getTradeLevel();

    void setTradeLevel(int level);

    ResourceLocation getProfessionId();

    void setProfessionId(ResourceLocation id);


}
