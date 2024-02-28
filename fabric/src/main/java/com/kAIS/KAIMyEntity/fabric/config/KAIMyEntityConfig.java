package com.kAIS.KAIMyEntity.fabric.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

public final class KAIMyEntityConfig {
    public static boolean openGLEnableLighting = true;
    public static int modelPoolMaxCount = 100;
    public static boolean isMDDShaderEnabled = false;

    static {
        try (BufferedReader reader = Files.newBufferedReader(FabricLoader.getInstance().getConfigDir().resolve("KAIMyEntity.properties"))) {
            Properties properties = new Properties();
            properties.load(reader);
            KAIMyEntityConfig.openGLEnableLighting = Boolean.parseBoolean(properties.getProperty("openGLEnableLighting"));
            KAIMyEntityConfig.modelPoolMaxCount = Integer.parseInt(properties.getProperty("modelPoolMaxCount"));
            KAIMyEntityConfig.isMDDShaderEnabled = Boolean.parseBoolean(properties.getProperty("isMDDShaderEnabled"));
        } catch (Exception ignored) {
            try (BufferedWriter writer = Files.newBufferedWriter(FabricLoader.getInstance().getConfigDir().resolve("KAIMyEntity.properties"))){
                Properties properties = new Properties();
                properties.setProperty("openGLEnableLighting", "true");
                properties.setProperty("modelPoolMaxCount",    "100");
                properties.setProperty("isMDDShaderEnabled",   "falase");
                properties.store(writer, properties.toString());
            } catch (IOException e){
                System.out.println(e);
            }
        }
    }
}
