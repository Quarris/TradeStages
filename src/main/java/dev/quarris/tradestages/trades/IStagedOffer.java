package dev.quarris.tradestages.trades;

import net.minecraft.resources.ResourceLocation;

public interface IStagedOffer {

    int getTradeLevel();

    void setTradeLevel(int level);

    ResourceLocation getProfessionId();

    void setProfessionId(ResourceLocation id);


}
