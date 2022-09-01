package dev.quarris.tradestages;

import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class TradeStagesData extends SavedData {

    public static TradeStagesData get(MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage().get(TradeStagesData::load, "tradestages");
    }

    private final Map<Pair<String, Integer>, String> stages;

    public TradeStagesData(Map<Pair<String, Integer>, String> stages) {
        this.stages = stages;
    }

    public boolean canTrade(String profession, int tradeLevel, Player player) {
        return GameStageHelper.hasStage(player, this.stages.get(Pair.of(profession, tradeLevel)));
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag stagesListTag = new ListTag();
        for (Map.Entry<Pair<String, Integer>, String> entry : this.stages.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("Profession", entry.getKey().getLeft());
            entryTag.putInt("TradeLevel", entry.getKey().getRight());
            entryTag.putString("Stage", entry.getValue());
            stagesListTag.add(entryTag);
        }
        tag.put("Stages", stagesListTag);
        return tag;
    }

    public static TradeStagesData load(CompoundTag tag) {
        Map<Pair<String, Integer>, String> stages = new HashMap<>();
        ListTag stagesListTag = tag.getList("Stages", Tag.TAG_COMPOUND);
        for (Tag baseTag : stagesListTag) {
            if (baseTag instanceof CompoundTag entryTag) {
                String profession = entryTag.getString("Profession");
                int tradeLevel = entryTag.getInt("TradeLevel");
                String stage = entryTag.getString("Stage");
                stages.put(Pair.of(profession, tradeLevel), stage);
            }
        }

        return new TradeStagesData(stages);
    }
}
