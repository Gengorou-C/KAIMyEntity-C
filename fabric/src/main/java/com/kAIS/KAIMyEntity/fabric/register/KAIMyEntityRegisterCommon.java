package com.kAIS.KAIMyEntity.register;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class KAIMyEntityRegisterCommon {
    public static Identifier KAIMYENTITY_C2S = new Identifier("kaimyentity", "network_c2s");
    public static Identifier KAIMYENTITY_S2C = new Identifier("kaimyentity", "network_s2c");

    public static void Register() {
        ServerPlayNetworking.registerGlobalReceiver(KAIMYENTITY_C2S, (server, player, handler, buf, responseSender) -> {
            PacketByteBuf packetbuf = PacketByteBufs.create();
            packetbuf.writeInt(buf.readInt());
            packetbuf.writeUuid(buf.readUuid());
            packetbuf.writeInt(buf.readInt());
            server.execute(() -> {
                for(ServerPlayerEntity serverPlayer : PlayerLookup.all(server)){
                    if(!serverPlayer.equals(player)){
                        ServerPlayNetworking.send(serverPlayer, KAIMyEntityRegisterCommon.KAIMYENTITY_S2C, packetbuf);
                    }
                }
            });
        });
    }
}
