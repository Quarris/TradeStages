package dev.quarris.tradestages;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.quarris.tradestages.trades.IStagedOffer;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StagedTradeData {

    private final Table<Integer, ResourceLocation, List<String>> stagedTrades = HashBasedTable.create();

    public List<String> getStages(int tradeLevel, ResourceLocation profession) {
        return stagedTrades.get(tradeLevel, profession);
    }

    public boolean canTrade(Player player, MerchantOffer offer) {
        if (!(offer instanceof IStagedOffer stagedOffer)) {
            return true;
        }

        List<String> stages = this.stagedTrades.get(stagedOffer.getTradeLevel(), stagedOffer.getProfessionId());
        if (stages == null || stages.isEmpty()) {
            return true;
        }

        return GameStageHelper.hasAnyOf(player, stages);
    }

    public static StagedTradeData load(JsonObject json) {
        StagedTradeData data = new StagedTradeData();
        for (Map.Entry<String, JsonElement> professionEntry : json.entrySet()) {
            ResourceLocation profession = new ResourceLocation(professionEntry.getKey());
            if (!professionEntry.getValue().isJsonObject()) {
                ModRef.LOGGER.warn("Profession Entry '{}' has to be a Json Object, was {}", profession, professionEntry.getValue());
                continue;
            }
            for (Map.Entry<String, JsonElement> tradeLevelEntry : professionEntry.getValue().getAsJsonObject().entrySet()) {
                int tradeLevel;
                try {
                    tradeLevel = Integer.parseInt(tradeLevelEntry.getKey());
                } catch (NumberFormatException e) {
                    ModRef.LOGGER.warn("Trade level has to be a whole number, was {}", tradeLevelEntry.getKey());
                    continue;
                }
                if (!tradeLevelEntry.getValue().isJsonArray()) {
                    ModRef.LOGGER.warn("Trade Level Entry '{}' has to be a Json Object, was {}", profession, professionEntry.getValue());
                    continue;
                }

                List<String> stages = new ArrayList<>();
                for (JsonElement stageJson : tradeLevelEntry.getValue().getAsJsonArray()) {
                    if (!stageJson.isJsonPrimitive() || !stageJson.getAsJsonPrimitive().isString()) {
                        ModRef.LOGGER.warn("Stage has to be a String, was {}", stageJson);
                        continue;
                    }

                    stages.add(stageJson.getAsString());
                }

                data.stagedTrades.put(tradeLevel, profession, stages);
            }
        }

        return data;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        ListTag tableNbt = new ListTag();
        for (int tradeLevel : this.stagedTrades.rowKeySet()) {
            for (ResourceLocation profession : this.stagedTrades.columnKeySet()) {
                ListTag stagesList = new ListTag();
                this.stagedTrades.get(tradeLevel, profession).forEach(stage -> stagesList.add(StringTag.valueOf(stage)));

                CompoundTag tradeNbt = new CompoundTag();
                tradeNbt.putInt("TradeLevel", tradeLevel);
                tradeNbt.putString("Profession", profession.toString());
                tradeNbt.put("Stages", stagesList);
                tableNbt.add(tradeNbt);
            }
        }
        tag.put("Table", tableNbt);

        return tag;
    }

    public void deserialize(CompoundTag tag) {
        this.stagedTrades.clear();
        ListTag tableNbt = tag.getList("Table", CompoundTag.TAG_COMPOUND);
        for (Tag baseTag : tableNbt) {
            CompoundTag tradeNbt = (CompoundTag) baseTag;
            int tradeLevel = tradeNbt.getInt("TradeLevel");
            ResourceLocation profession = new ResourceLocation(tradeNbt.getString("Profession"));
            List<String> stages = tradeNbt.getList("Stages", CompoundTag.TAG_STRING).stream().map(Tag::getAsString).toList();
            this.stagedTrades.put(tradeLevel, profession, stages);
        }
    }

    @Override
    public String toString() {
        return this.stagedTrades.toString();
    }
}
