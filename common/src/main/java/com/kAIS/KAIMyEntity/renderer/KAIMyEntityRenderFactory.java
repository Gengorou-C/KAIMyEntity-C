package com.kAIS.KAIMyEntity.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;

public class KAIMyEntityRenderFactory<T extends Entity> implements EntityRendererProvider<T> {
    String entityName;

    public KAIMyEntityRenderFactory(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public EntityRenderer<T> create(Context manager) {
        return new KAIMyEntityRenderer<>(manager, entityName);
    }
}
