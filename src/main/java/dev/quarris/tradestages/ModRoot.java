package dev.quarris.tradestages;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Mod(ModRef.ID)
public class ModRoot {

    public static AbstractVillager lastInteractedVillager;
    public static StagedTradeData stagedTrades;

    public ModRoot() {
        loadTradeData();
        System.out.println(stagedTrades);
    }

    public static void loadTradeData() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(FMLPaths.CONFIGDIR.get().resolve("tradestages.json").toFile()));
            JsonObject configJson = GsonHelper.parse(reader);
            if (!configJson.has("trades") || !configJson.get("trades").isJsonObject()) {
                ModRef.LOGGER.warn("Config file must contain 'trades' as Json Object");
                stagedTrades = new StagedTradeData();
                return;
            }
            stagedTrades = StagedTradeData.load(configJson.getAsJsonObject("trades"));
        } catch (Exception e1) {
            ModRef.LOGGER.error("Failed to read config", e1);
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e2) {
                    ModRef.LOGGER.error("Failed to close config reader", e2);
                }
            }
        }
    }
}
