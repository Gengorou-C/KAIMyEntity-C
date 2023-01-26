package com.kAIS.KAIMyEntity;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kAIS.KAIMyEntity.register.KAIMyEntityRegisterCommon;

public class KAIMyEntity implements ModInitializer {
    public static final Logger logger = LogManager.getLogger();

    @Override
    public void onInitialize() {
        logger.info("KAIMyEntity Init begin...");
        KAIMyEntityRegisterCommon.Register();
        logger.info("KAIMyEntity Init successful.");
    }
}
