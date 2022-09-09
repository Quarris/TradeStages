package dev.quarris.tradestages;

import com.google.gson.JsonObject;
import dev.quarris.tradestages.network.ClientboundTradeDataPacket;
import dev.quarris.tradestages.network.PacketManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLanguageProvider;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Mod(ModRef.ID)
public class ModRoot {

    public static AbstractVillager lastInteractedVillager;
    public static StagedTradeData stagedTrades;
    public static boolean hideLockedTrades;

    public ModRoot() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        loadTradeData();
        System.out.println(stagedTrades);
    }

    public static ClientboundTradeDataPacket createTradeDataPacket() {
        return new ClientboundTradeDataPacket(hideLockedTrades, stagedTrades.serialize());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        PacketManager.init();
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
            hideLockedTrades = GsonHelper.getAsBoolean(configJson, "hideLockedTrades", false);
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
