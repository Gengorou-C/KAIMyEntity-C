package com.kAIS.KAIMyEntity;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KAIMyEntity implements ModInitializer {
    public static final Logger logger = LogManager.getLogger();
    public static int usingMMDShader = 0;
    public static boolean reloadProperties = false;

    @Override
    public void onInitialize() {
    }
}
