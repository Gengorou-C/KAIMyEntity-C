package com.kAIS.KAIMyEntity.neoforge.network;

import com.kAIS.KAIMyEntity.renderer.KAIMyEntityRendererPlayerHelper;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KAIMyEntityNetworkPack implements CustomPacketPayload{
    public static final Logger logger = LogManager.getLogger();
    public static ResourceLocation id = new ResourceLocation("kaimyentity", "networkpack");
    public int opCode;
    public UUID playerUUID;
    public int arg0;

    public KAIMyEntityNetworkPack(int opCode, UUID playerUUID, int arg0) {
        this.opCode = opCode;
        this.playerUUID = playerUUID;
        this.arg0 = arg0;
    }

    public KAIMyEntityNetworkPack(FriendlyByteBuf buffer) {
        opCode = buffer.readInt();
        playerUUID = new UUID(buffer.readLong(), buffer.readLong());
        arg0 = buffer.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer){
        buffer.writeInt(opCode);
        buffer.writeLong(playerUUID.getMostSignificantBits());
        buffer.writeLong(playerUUID.getLeastSignificantBits());
        buffer.writeInt(arg0);
    }

    @Override
    public ResourceLocation id(){
        return id;
    }

    public void DoInClient() {
        Minecraft MCinstance = Minecraft.getInstance();
        //Ignore message when player is self.
        assert MCinstance.player != null;
        assert MCinstance.level != null;
        Player targetPlayer = MCinstance.level.getPlayerByUUID(playerUUID);
        if (playerUUID.equals(MCinstance.player.getUUID()))
            return;
        if (targetPlayer == null){
            logger.warn("received an invalid UUID.");
            return;
        }
        switch (opCode) {
            case 1: {
                RenderSystem.recordRenderCall(()->{
                MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + targetPlayer.getName().getString());
                if (m != null)
                    KAIMyEntityRendererPlayerHelper.CustomAnim(targetPlayer, Integer.toString(arg0));
                });
                break;
            }
            case 2: {
                RenderSystem.recordRenderCall(()->{
                MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + targetPlayer.getName().getString());
                if (m != null)
                    KAIMyEntityRendererPlayerHelper.ResetPhysics(targetPlayer);
                });
                break;
            }
        }
    }

    public void DoInServer(){
        PacketDistributor.ALL.noArg().send(this);
    }
}