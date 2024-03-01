package com.kAIS.KAIMyEntity.neoforge;

import com.kAIS.KAIMyEntity.KAIMyEntity;
import com.kAIS.KAIMyEntity.neoforge.config.KAIMyEntityConfig;
import com.kAIS.KAIMyEntity.neoforge.register.KAIMyEntityRegisterCommon;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(KAIMyEntity.MOD_ID)
public class KAIMyEntityNeoForge {
    public static final Logger logger = LogManager.getLogger();
    //public static String[] debugStr = new String[hogehoge];
    public KAIMyEntityNeoForge(IEventBus eventBus) {
        logger.info("KAIMyEntity Init begin...");
        eventBus.addListener(KAIMyEntityRegisterCommon::Register);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, KAIMyEntityConfig.config);
        logger.info("KAIMyEntity Init successful.");
    }
}