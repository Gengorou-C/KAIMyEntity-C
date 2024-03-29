package com.kAIS.KAIMyEntity.fabric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kAIS.KAIMyEntity.fabric.register.KAIMyEntityRegisterCommon;
import net.fabricmc.api.ModInitializer;

public class KAIMyEntityFabric implements ModInitializer {
    public static final Logger logger = LogManager.getLogger();
    @Override
    public void onInitialize() {
        logger.info("KAIMyEntity Init begin...");
        KAIMyEntityRegisterCommon.Register();
        logger.info("KAIMyEntity Init successful.");
    }
}
