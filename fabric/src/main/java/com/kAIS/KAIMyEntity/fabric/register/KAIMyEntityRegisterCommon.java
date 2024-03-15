package com.kAIS.KAIMyEntity.register;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class KAIMyEntityRegisterCommon {
    public static ResourceLocation KAIMYENTITY_C2S = new ResourceLocation("kaimyentity", "network_c2s");
    public static ResourceLocation KAIMYENTITY_S2C = new ResourceLocation("kaimyentity", "network_s2c");

    public static void Register() {
        ServerPlayNetworking.registerGlobalReceiver(KAIMYENTITY_C2S, (server, player, handler, buf, responseSender) -> {
            FriendlyByteBuf packetbuf = PacketByteBufs.create();
            packetbuf.writeInt(buf.readInt());
            packetbuf.writeUUID(buf.readUUID());
            packetbuf.writeInt(buf.readInt());
            server.execute(() -> {
                for(ServerPlayer serverPlayer : PlayerLookup.all(server)){
                    if(!serverPlayer.equals(player)){
                        ServerPlayNetworking.send(serverPlayer, KAIMyEntityRegisterCommon.KAIMYENTITY_S2C, packetbuf);
                    }
                }
            });
        });
    }
}