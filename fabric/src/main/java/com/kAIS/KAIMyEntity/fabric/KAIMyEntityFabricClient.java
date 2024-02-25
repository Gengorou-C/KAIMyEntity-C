package com.kAIS.KAIMyEntity.fabric;

import com.kAIS.KAIMyEntity.KAIMyEntity;
import com.kAIS.KAIMyEntity.fabric.register.KAIMyEntityRegisterClient;
import net.fabricmc.api.ClientModInitializer;

public class KAIMyEntityFabricClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
      KAIMyEntity.logger.info("KAIMyEntity InitClient begin...");
      KAIMyEntity.initClient();
      KAIMyEntityRegisterClient.Register();
      KAIMyEntity.logger.info("KAIMyEntity InitClient successful.");
  }
}
