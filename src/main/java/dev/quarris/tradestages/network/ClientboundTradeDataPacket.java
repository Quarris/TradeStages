package dev.quarris.tradestages.network;

import dev.quarris.tradestages.ModRoot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundTradeDataPacket {

    private boolean hiddenTrades;
    private CompoundTag tradeNbt;

    public ClientboundTradeDataPacket(boolean hiddenTrades, CompoundTag tradeNbt) {
        this.hiddenTrades = hiddenTrades;
        this.tradeNbt = tradeNbt;
    }

    public static void encode(ClientboundTradeDataPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.hiddenTrades);
        buf.writeNbt(packet.tradeNbt);
    }

    public static ClientboundTradeDataPacket decode(FriendlyByteBuf buf) {
        return new ClientboundTradeDataPacket(buf.readBoolean(), buf.readNbt());
    }

    public static void handle(ClientboundTradeDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ModRoot.hideLockedTrades = packet.hiddenTrades;
            ModRoot.stagedTrades.deserialize(packet.tradeNbt);
        });
        ctx.get().setPacketHandled(true);
    }
}
