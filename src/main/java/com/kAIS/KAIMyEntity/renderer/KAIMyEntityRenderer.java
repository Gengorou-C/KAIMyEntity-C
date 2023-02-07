package com.kAIS.KAIMyEntity.renderer;

import org.joml.Quaternionf;

import com.kAIS.KAIMyEntity.KAIMyEntityClient;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class KAIMyEntityRenderer<T extends Entity> extends EntityRenderer<T> {
    protected String modelName;
    protected EntityRendererFactory.Context context;

    public KAIMyEntityRenderer(EntityRendererFactory.Context renderManager, String entityName) {
        super(renderManager);
        this.modelName = entityName.replace(':', '.');
        this.context = renderManager;
    }

    @Override
    public boolean shouldRender(T livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        return super.shouldRender(livingEntityIn, camera, camX, camY, camZ);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        String animName;
        if (entityIn.hasPassengers() && (entityIn.getX() - entityIn.prevX != 0.0f || entityIn.getZ() - entityIn.prevZ != 0.0f)) {
            animName = "driven";
        } else if (entityIn.hasPassengers()) {
            animName = "ridden";
        } else if (entityIn.isSwimming()) {
            animName = "swim";
        } else if ( (entityIn.getX() - entityIn.prevX != 0.0f || entityIn.getZ() - entityIn.prevZ != 0.0f) && entityIn.getVehicle() == null) {
            animName = "walk";
        } else {
            animName = "idle";
        }
        MMDModelManager.Model model = MMDModelManager.GetNotPlayerModel(modelName, animName);
        if (model != null) {
            matrixStackIn.push();
            if(entityIn instanceof LivingEntity)
                if(((LivingEntity) entityIn).isBaby())
                    matrixStackIn.scale(0.5f, 0.5f, 0.5f);

            if(KAIMyEntityClient.calledFrom(6).contains("Inventory") || KAIMyEntityClient.calledFrom(6).contains("class_490")){ // net.minecraft.class_490 == net.minecraft.client.gui.screen.ingame.InventoryScreen
                RenderSystem.setShader(GameRenderer::getPositionTexProgram);
                MatrixStack PTS_modelViewStack = RenderSystem.getModelViewStack();
                PTS_modelViewStack.translate(0.0f, 0.0f, 1000.0f);
                PTS_modelViewStack.push();
                PTS_modelViewStack.scale(20.0f,20.0f, 20.0f);
                Quaternionf quaternionf = (new Quaternionf()).rotateZ((float)Math.PI);
                Quaternionf quaternionf1 = (new Quaternionf()).rotateX(-entityIn.getPitch() * ((float)Math.PI / 180F));
                Quaternionf quaternionf2 = (new Quaternionf()).rotateY(-entityIn.getYaw() * ((float)Math.PI / 180F));
                quaternionf.mul(quaternionf1);
                quaternionf.mul(quaternionf2);
                PTS_modelViewStack.multiply(quaternionf);
                RenderSystem.setShader(GameRenderer::getRenderTypeEntityCutoutNoNullProgram);
                model.model.Render(entityIn, entityYaw, 0.0f, PTS_modelViewStack, packedLightIn);
                PTS_modelViewStack.pop();
            }else{
                RenderSystem.setShader(GameRenderer::getRenderTypeEntityCutoutNoNullProgram);
                model.model.Render(entityIn, entityYaw, 0.0f, matrixStackIn, packedLightIn);
            }
            matrixStackIn.pop();
        }
    }

    @Override
    public Identifier getTexture(T entity) {
        return null;
    }
}
