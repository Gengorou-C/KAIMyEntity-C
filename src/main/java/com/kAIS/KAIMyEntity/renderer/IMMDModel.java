package com.kAIS.KAIMyEntity.renderer;

import org.joml.Vector3f;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public interface IMMDModel {
    void Render(Entity entityIn, float entityYaw, float entityPitch, Vector3f entityTrans, MatrixStack mat, int packedLight);

    void ChangeAnim(long anim, long layer);

    void ResetPhysics();

    long GetModelLong();

    String GetModelDir();
}