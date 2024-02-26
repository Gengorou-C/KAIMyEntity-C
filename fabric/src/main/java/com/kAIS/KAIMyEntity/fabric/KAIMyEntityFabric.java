package com.kAIS.KAIMyEntity.fabric;

import com.kAIS.KAIMyEntity.KAIMyEntityClient;
import com.kAIS.KAIMyEntity.fabric.register.KAIMyEntityRegisterCommon;
import net.fabricmc.api.ModInitializer;

public class KAIMyEntityFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        KAIMyEntityClient.logger.info("KAIMyEntity Init begin...");
        KAIMyEntityRegisterCommon.Register();
        KAIMyEntityClient.logger.info("KAIMyEntity Init successful.");
    }
}
