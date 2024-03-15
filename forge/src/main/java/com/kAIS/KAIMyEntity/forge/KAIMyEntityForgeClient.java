package com.kAIS.KAIMyEntity.forge;

import com.kAIS.KAIMyEntity.KAIMyEntity;
import com.kAIS.KAIMyEntity.KAIMyEntityClient;
import com.kAIS.KAIMyEntity.forge.config.KAIMyEntityConfig;
import com.kAIS.KAIMyEntity.forge.register.KAIMyEntityRegisterClient;
import com.kAIS.KAIMyEntity.renderer.MMDModelOpenGL;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = KAIMyEntity.MOD_ID)
public class KAIMyEntityForgeClient {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        KAIMyEntityClient.logger.info("KAIMyEntity InitClient begin...");
        KAIMyEntityClient.initClient();
        KAIMyEntityRegisterClient.Register();
        MMDModelOpenGL.isMMDShaderEnabled = KAIMyEntityConfig.isMMDShaderEnabled.get();
        KAIMyEntityClient.logger.info("KAIMyEntity InitClient successful.");
    }
}
