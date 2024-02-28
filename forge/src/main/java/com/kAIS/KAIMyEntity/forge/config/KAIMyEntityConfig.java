package com.kAIS.KAIMyEntity.forge.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class KAIMyEntityConfig {
    public static ForgeConfigSpec config;
    public static ForgeConfigSpec.BooleanValue openGLEnableLighting;
    public static ForgeConfigSpec.IntValue modelPoolMaxCount;
    public static ForgeConfigSpec.BooleanValue isMMDShaderEnabled;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("KAIMyEntity");
        openGLEnableLighting = builder.define("openGLEnableLighting", true);
        modelPoolMaxCount = builder.defineInRange("modelPoolMaxCount", 20, 0, 100);
        isMMDShaderEnabled = builder.define("isMMDShaderEnabled", false);
        builder.pop();
        config = builder.build();
    }
}