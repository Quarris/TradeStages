package dev.quarris.tradestages;

import dev.quarris.tradestages.network.PacketManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;

@Mod.EventBusSubscriber(modid = ModRef.ID)
public class EventHandler {

    @SubscribeEvent
    public static void sendDataToClient(PlayerEvent.PlayerLoggedInEvent event) {
        PacketManager.INST.sendTo(ModRoot.createTradeDataPacket(), ((ServerPlayer) event.getPlayer()).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

}
