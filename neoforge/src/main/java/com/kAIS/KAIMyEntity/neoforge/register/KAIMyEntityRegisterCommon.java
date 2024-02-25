package com.kAIS.KAIMyEntity.neoforge.register;

import com.kAIS.KAIMyEntity.neoforge.network.KAIMyEntityNetworkPack;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class KAIMyEntityRegisterCommon {
    public static SimpleChannel channel;
    static String networkVersion = "1";

    public static void Register() {
        channel = NetworkRegistry.newSimpleChannel(new ResourceLocation("kaimyentity", "network_pack"), () -> networkVersion, NetworkRegistry.acceptMissingOr(networkVersion), (version) -> version.equals(networkVersion));
        channel.messageBuilder(KAIMyEntityNetworkPack.class, 0).encoder(KAIMyEntityNetworkPack::Pack).decoder(KAIMyEntityNetworkPack::new).consumerMainThread(KAIMyEntityNetworkPack::Do).add();
    } 
}