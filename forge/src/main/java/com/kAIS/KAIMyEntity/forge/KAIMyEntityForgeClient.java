package com.kAIS.KAIMyEntity.forge;

import com.kAIS.KAIMyEntity.KAIMyEntityClient;
import com.kAIS.KAIMyEntity.forge.register.KAIMyEntityRegisterClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = KAIMyEntityClient.MOD_ID)
public class KAIMyEntityForgeClient {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        KAIMyEntityClient.logger.info("KAIMyEntity InitClient begin...");
        KAIMyEntityClient.initClient();
        KAIMyEntityRegisterClient.Register();
        KAIMyEntityClient.logger.info("KAIMyEntity InitClient successful.");
    }
}
