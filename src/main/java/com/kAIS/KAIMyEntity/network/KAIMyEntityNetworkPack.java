package com.kAIS.KAIMyEntity.network;

import java.util.UUID;

import com.kAIS.KAIMyEntity.register.KAIMyEntityRegisterCommon;
import com.kAIS.KAIMyEntity.renderer.KAIMyEntityRendererPlayerHelper;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class KAIMyEntityNetworkPack {
    public static void sendToServer(int opCode, UUID playerUUID, int arg0){
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(opCode);
        buffer.writeUuid(playerUUID);
        buffer.writeInt(arg0);
        ClientPlayNetworking.send(KAIMyEntityRegisterCommon.KAIMYENTITY_C2S, buffer);
    }
    
    public static void DoInClient(PacketByteBuf buffer){
        DoInClient(buffer.readInt(), buffer.readUuid(), buffer.readInt());
    }

    public static void DoInClient(int opCode, UUID playerUUID, int arg0) {
        //Ignore message when player is self.
        assert MinecraftClient.getInstance().player != null;
        if (playerUUID.equals(MinecraftClient.getInstance().player.getUuid()))
            return;
        switch (opCode) {
            case 1: {
                MMDModelManager.Model m = MMDModelManager.GetPlayerModel("EntityPlayer_" + MinecraftClient.getInstance().player.getName().getString());
                assert MinecraftClient.getInstance().world != null;
                PlayerEntity target = MinecraftClient.getInstance().world.getPlayerByUuid(playerUUID);
                if (m != null && target != null)
                    KAIMyEntityRendererPlayerHelper.CustomAnim(target, Integer.toString(arg0));
                break;
            }
            case 2: {
                MMDModelManager.Model m = MMDModelManager.GetPlayerModel("EntityPlayer_" + MinecraftClient.getInstance().player.getName().getString());
                assert MinecraftClient.getInstance().world != null;
                PlayerEntity target = MinecraftClient.getInstance().world.getPlayerByUuid(playerUUID);
                if (m != null && target != null)
                    KAIMyEntityRendererPlayerHelper.ResetPhysics(target);
                break;
            }
        }
    }
}
