package com.kAIS.KAIMyEntity.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.kAIS.KAIMyEntity.register.KAIMyEntityRegisterCommon;
import com.kAIS.KAIMyEntity.renderer.KAIMyEntityRendererPlayerHelper;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

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

    public void Do(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                {
                    if (FMLEnvironment.dist == Dist.CLIENT) {
                        DoInClient();
                    } else {
                        KAIMyEntityRegisterCommon.channel.send(PacketDistributor.ALL.noArg(), this);
                    }
                }
        );
        ctx.get().setPacketHandled(true);
    }

    public void DoInClient() {
        Minecraft MCinstance = Minecraft.getInstance();
        //Ignore message when player is self.
        assert Minecraft.getInstance().player != null;
        if (playerUUID.equals(MCinstance.player.getUUID()))
            return;
        switch (opCode) {
            case 1: {
                MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + MCinstance.player.getName().getString());
                assert Minecraft.getInstance().level != null;
                Player target = MCinstance.level.getPlayerByUUID(playerUUID);
                if (m != null && target != null)
                    KAIMyEntityRendererPlayerHelper.CustomAnim(target, Integer.toString(arg0));
                break;
            }
            case 2: {
                MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + MCinstance.player.getName().getString());
                assert Minecraft.getInstance().level != null;
                Player target = MCinstance.level.getPlayerByUUID(playerUUID);
                if (m != null && target != null)
                    KAIMyEntityRendererPlayerHelper.ResetPhysics(target);
                break;
            }
        }
    }
}