package com.kAIS.KAIMyEntity.neoforge;

import com.kAIS.KAIMyEntity.KAIMyEntity;
import com.kAIS.KAIMyEntity.neoforge.register.KAIMyEntityRegisterClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = KAIMyEntity.MOD_ID)
public class KAIMyEntityNeoForgeClient {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        KAIMyEntity.logger.info("KAIMyEntity InitClient begin...");
        KAIMyEntity.initClient();
        KAIMyEntityRegisterClient.Register();
        KAIMyEntity.logger.info("KAIMyEntity InitClient successful.");
    }
}
