package com.kAIS.KAIMyEntity.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class KAIMyEntityConfig {
    public static ForgeConfigSpec config;
    public static ForgeConfigSpec.BooleanValue openGLEnableLighting;
    public static ForgeConfigSpec.IntValue modelPoolMaxCount;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("KAIMyEntity");
        openGLEnableLighting = builder.define("openGLEnableLighting", true);
        modelPoolMaxCount = builder.defineInRange("modelPoolMaxCount", 20, 0, 100);
        builder.pop();
        config = builder.build();
    }
}