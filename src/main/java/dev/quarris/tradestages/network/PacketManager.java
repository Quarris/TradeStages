package dev.quarris.tradestages.network;

import dev.quarris.tradestages.ModRef;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class PacketManager {

    private static final String VERSION = "1";
    public static final SimpleChannel INST = NetworkRegistry.newSimpleChannel(
        ModRef.res("channel"), () -> VERSION, VERSION::equals, VERSION::equals
    );

    public static void init() {
        INST.registerMessage(0, ClientboundTradeDataPacket.class, ClientboundTradeDataPacket::encode, ClientboundTradeDataPacket::decode, ClientboundTradeDataPacket::handle);
    }

}
