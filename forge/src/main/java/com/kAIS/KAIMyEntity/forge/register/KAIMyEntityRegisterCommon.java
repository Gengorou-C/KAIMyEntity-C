package com.kAIS.KAIMyEntity.forge.register;

import com.kAIS.KAIMyEntity.forge.network.KAIMyEntityNetworkPack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.network.ChannelBuilder;

public class KAIMyEntityRegisterCommon {
    public static SimpleChannel channel;
    static String networkVersion = "1";

    public static void Register() {
        channel = ChannelBuilder.named(new ResourceLocation("kaimyentity", "network_pack")).networkProtocolVersion(1).optional().simpleChannel();
        channel.messageBuilder(KAIMyEntityNetworkPack.class).encoder(KAIMyEntityNetworkPack::Pack).decoder(KAIMyEntityNetworkPack::new).consumerMainThread(KAIMyEntityNetworkPack::Do).add();
    }
}