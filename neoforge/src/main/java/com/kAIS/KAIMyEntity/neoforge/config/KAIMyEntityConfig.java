package com.kAIS.KAIMyEntity.neoforge.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class KAIMyEntityConfig {
    public static ModConfigSpec config;
    public static ModConfigSpec.BooleanValue openGLEnableLighting;
    public static ModConfigSpec.IntValue modelPoolMaxCount;
    public static ModConfigSpec.BooleanValue isMMDShaderEnabled;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("KAIMyEntity");
        openGLEnableLighting = builder.define("openGLEnableLighting", true);
        modelPoolMaxCount = builder.defineInRange("modelPoolMaxCount", 20, 0, 100);
        isMMDShaderEnabled = builder.define("isMMDShaderEnabled", false);
        builder.pop();
        config = builder.build();
    }
}