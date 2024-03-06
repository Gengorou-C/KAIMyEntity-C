package com.kAIS.KAIMyEntity.register;

import com.kAIS.KAIMyEntity.network.KAIMyEntityNetworkPack;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import net.minecraft.resources.ResourceLocation;

public class KAIMyEntityRegisterCommon {
    public static SimpleChannel channel;
    static String networkVersion = "1";

    public static void Register() {
        channel = NetworkRegistry.newSimpleChannel(new ResourceLocation("kaimyentity", "network_pack"), () -> networkVersion, NetworkRegistry.acceptMissingOr(networkVersion), (version) -> version.equals(networkVersion));
        channel.registerMessage(0, KAIMyEntityNetworkPack.class, KAIMyEntityNetworkPack::Pack, KAIMyEntityNetworkPack::new, KAIMyEntityNetworkPack::Do);
    }
}