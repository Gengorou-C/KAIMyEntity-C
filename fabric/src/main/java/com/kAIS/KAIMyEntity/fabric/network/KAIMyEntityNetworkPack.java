package com.kAIS.KAIMyEntity.network;

import com.kAIS.KAIMyEntity.register.KAIMyEntityRegisterCommon;
import com.kAIS.KAIMyEntity.renderer.KAIMyEntityRendererPlayerHelper;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager;
import java.util.UUID;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class KAIMyEntityNetworkPack {
    public static void sendToServer(int opCode, UUID playerUUID, int arg0){
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(opCode);
        buffer.writeUUID(playerUUID);
        buffer.writeInt(arg0);
        ClientPlayNetworking.send(KAIMyEntityRegisterCommon.KAIMYENTITY_C2S, buffer);
    }
    
    public static void DoInClient(FriendlyByteBuf buffer){
        DoInClient(buffer.readInt(), buffer.readUUID(), buffer.readInt());
    }

    public static void DoInClient(int opCode, UUID playerUUID, int arg0) {
        Minecraft MCinstance = Minecraft.getInstance();
        //Ignore message when player is self.
        assert MCinstance.player != null;
        if (playerUUID.equals(MCinstance.player.getUUID()))
            return;
        switch (opCode) {
            case 1: {
                MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + MCinstance.player.getName().getString());
                assert MCinstance.level != null;
                Player target = MCinstance.level.getPlayerByUUID(playerUUID);
                if (m != null && target != null)
                    KAIMyEntityRendererPlayerHelper.CustomAnim(target, Integer.toString(arg0));
                break;
            }
            case 2: {
                MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + MCinstance.player.getName().getString());
                assert MCinstance.level != null;
                Player target = MCinstance.level.getPlayerByUUID(playerUUID);
                if (m != null && target != null)
                    KAIMyEntityRendererPlayerHelper.ResetPhysics(target);
                break;
            }
        }
    }
}
