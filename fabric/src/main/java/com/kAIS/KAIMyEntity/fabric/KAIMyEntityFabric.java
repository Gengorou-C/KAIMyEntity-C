package com.kAIS.KAIMyEntity.fabric;

import com.kAIS.KAIMyEntity.KAIMyEntity;
import com.kAIS.KAIMyEntity.fabric.register.KAIMyEntityRegisterCommon;
import net.fabricmc.api.ModInitializer;

public class KAIMyEntityFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        KAIMyEntity.logger.info("KAIMyEntity Init begin...");
        KAIMyEntityRegisterCommon.Register();
        KAIMyEntity.logger.info("KAIMyEntity Init successful.");
    }
}
