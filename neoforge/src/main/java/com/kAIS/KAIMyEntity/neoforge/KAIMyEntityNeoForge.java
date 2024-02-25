package com.kAIS.KAIMyEntity.neoforge;

import com.kAIS.KAIMyEntity.KAIMyEntity;
import com.kAIS.KAIMyEntity.neoforge.config.KAIMyEntityConfig;
import com.kAIS.KAIMyEntity.neoforge.register.KAIMyEntityRegisterCommon;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(KAIMyEntity.MOD_ID)
public class KAIMyEntityNeoForge {
    //public static String[] debugStr = new String[hogehoge];
    public KAIMyEntityNeoForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, KAIMyEntityConfig.config);
    }

    public void preInit(FMLCommonSetupEvent event) {
        KAIMyEntity.logger.info("KAIMyEntity preInit begin...");
        KAIMyEntityRegisterCommon.Register();
        KAIMyEntity.logger.info("KAIMyEntity preInit successful.");
    }
}