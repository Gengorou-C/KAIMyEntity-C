package com.kAIS.KAIMyEntity.renderer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public interface IMMDModel {
    void Render(Entity entityIn, float entityYaw, MatrixStack mat, int packedLight);

    void ChangeAnim(long anim, long layer);

    void ResetPhysics();

    long GetModelLong();

    String GetModelDir();
}