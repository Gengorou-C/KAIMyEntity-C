package com.kAIS.KAIMyEntity.neoforge.network;

import com.kAIS.KAIMyEntity.KAIMyEntity;
import com.kAIS.KAIMyEntity.renderer.KAIMyEntityRendererPlayerHelper;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager;
import com.kAIS.KAIMyEntity.neoforge.register.KAIMyEntityRegisterCommon;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class KAIMyEntityNetworkPack {
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

    public void Pack(FriendlyByteBuf buffer) {
        buffer.writeInt(opCode);
        buffer.writeLong(playerUUID.getMostSignificantBits());
        buffer.writeLong(playerUUID.getLeastSignificantBits());
        buffer.writeInt(arg0);
    }

    
    public void Do(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() ->
                {
                    if (FMLEnvironment.dist == Dist.CLIENT) {
                        DoInClient();
                    } else {
                        KAIMyEntityRegisterCommon.channel.send(PacketDistributor.ALL.noArg(), this);
                    }
                }
        );
        ctx.setPacketHandled(true);
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
            KAIMyEntity.logger.warn("received an invalid UUID.");
            return;
        }
        switch (opCode) {
            case 1: {
                MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + targetPlayer.getName().getString());
                if (m != null)
                    KAIMyEntityRendererPlayerHelper.CustomAnim(targetPlayer, Integer.toString(arg0));
                break;
            }
            case 2: {
                MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + targetPlayer.getName().getString());
                if (m != null)
                    KAIMyEntityRendererPlayerHelper.ResetPhysics(targetPlayer);
                break;
            }
        }
    }
}