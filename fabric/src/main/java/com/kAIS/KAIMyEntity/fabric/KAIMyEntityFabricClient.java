package com.kAIS.KAIMyEntity.fabric;

import com.kAIS.KAIMyEntity.KAIMyEntityClient;
import com.kAIS.KAIMyEntity.fabric.config.KAIMyEntityConfig;
import com.kAIS.KAIMyEntity.fabric.register.KAIMyEntityRegisterClient;
import com.kAIS.KAIMyEntity.renderer.MMDModelOpenGL;

import net.fabricmc.api.ClientModInitializer;

public class KAIMyEntityFabricClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
      KAIMyEntityClient.logger.info("KAIMyEntity InitClient begin...");
      KAIMyEntityClient.initClient();
      KAIMyEntityRegisterClient.Register();
      MMDModelOpenGL.isMMDShaderEnabled = KAIMyEntityConfig.isMMDShaderEnabled;
      KAIMyEntityClient.logger.info("KAIMyEntity InitClient successful.");
  }
}
