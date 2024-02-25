package com.kAIS.KAIMyEntity;

import com.kAIS.KAIMyEntity.config.KAIMyEntityConfig;
import com.kAIS.KAIMyEntity.register.KAIMyEntityRegisterCommon;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("kaimyentity")
public class KAIMyEntity {
    public static final String MODID = "kaimyentity";
    public static Logger logger = LogManager.getLogger("KAIMyEntity");
    //public static String[] debugStr = new String[hogehoge];

    public KAIMyEntity() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, KAIMyEntityConfig.config);
    }

    public void preInit(FMLCommonSetupEvent event) {
        logger.info("KAIMyEntity preInit begin...");
        KAIMyEntityRegisterCommon.Register();
        logger.info("KAIMyEntity preInit successful.");
    }
}