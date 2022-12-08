package com.kAIS.KAIMyEntity;

import com.kAIS.KAIMyEntity.register.KAIMyEntityRegisterClient;
import com.kAIS.KAIMyEntity.renderer.MMDAnimManager;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager;
import com.kAIS.KAIMyEntity.renderer.MMDTextureManager;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KAIMyEntityClient implements ClientModInitializer {
    public static final Logger logger = LogManager.getLogger();

    @Override
    public void onInitializeClient() {
        logger.info("KAIMyEntity Init begin...");
        MMDModelManager.Init();
        logger.info("MMDModelManager.Init() finished");
        MMDTextureManager.Init();
        logger.info("MMDTextureManager.Init() finished");
        MMDAnimManager.Init();
        logger.info("MMDAnimManager.Init() finished");
        KAIMyEntityRegisterClient.Register();
        logger.info("KAIMyEntityRegisterClient.Register() finished");
        logger.info("KAIMyEntity preInit successful.");
    }
}
