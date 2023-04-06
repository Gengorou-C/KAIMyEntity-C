package com.kAIS.KAIMyEntity.renderer;

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
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

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
        String animName = "";
        float bodyYaw = entityYaw;
        float bodyPitch = 0.0f;
        Vec3f entityTrans = new Vec3f(0.0f, 0.0f, 0.0f);
        MMDModelManager.Model model = MMDModelManager.GetModel(modelName, entityIn.getUuidAsString());
        if(model == null){
            return;
        }
        MMDModelManager.ModelWithEntityData mwed = (MMDModelManager.ModelWithEntityData)model;
        model.loadModelProperties(false);
        float[] size = sizeOfModel(model);
        matrixStackIn.push();
        if(entityIn instanceof LivingEntity){
            if(((LivingEntity) entityIn).getHealth() <= 0.0F){
                animName = "die";
                AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Die, 0);
            }else if(((LivingEntity) entityIn).isSleeping()){
                animName = "sleep";
                bodyYaw = ((LivingEntity) entityIn).getSleepingDirection().asRotation() + 180.0f;
                bodyPitch = model.properties.getProperty("sleepingPitch") == null ? 0.0f : Float.valueOf(model.properties.getProperty("sleepingPitch"));
                entityTrans = model.properties.getProperty("sleepingTrans") == null ? new Vec3f(0.0f, 0.0f, 0.0f) : KAIMyEntityClient.str2Vec3f(model.properties.getProperty("sleepingTrans"));
                AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Sleep, 0);
            }
            if(((LivingEntity) entityIn).isBaby()){
                matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            }
        }
        if(animName == ""){
            if (entityIn.hasPassengers() && (entityIn.getX() - entityIn.prevX != 0.0f || entityIn.getZ() - entityIn.prevZ != 0.0f)) {
                animName = "driven";
                AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Driven, 0);
            } else if (entityIn.hasPassengers()) {
                animName = "ridden";
                AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Ridden, 0);
            } else if (entityIn.isSwimming()) {
                animName = "swim";
                AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Swim, 0);
            } else if ( (entityIn.getX() - entityIn.prevX != 0.0f || entityIn.getZ() - entityIn.prevZ != 0.0f) && entityIn.getVehicle() == null) {
                animName = "walk";
                AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Walk, 0);
            } else {
                animName = "idle";
                AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Idle, 0);
            }
        }
        if(KAIMyEntityClient.calledFrom(6).contains("Inventory") || KAIMyEntityClient.calledFrom(6).contains("class_490")){ // net.minecraft.class_490 == net.minecraft.client.gui.screen.ingame.InventoryScreen
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            MatrixStack PTS_modelViewStack = RenderSystem.getModelViewStack();
            PTS_modelViewStack.translate(0.0f, 0.0f, 1000.0f);
            PTS_modelViewStack.push();
            PTS_modelViewStack.scale(20.0f,20.0f, 20.0f);
            PTS_modelViewStack.scale(size[1], size[1], size[1]);
            Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0f);
            Quaternion quaternion1 = Vec3f.POSITIVE_X.getDegreesQuaternion(-entityIn.getPitch());
            Quaternion quaternion2 = Vec3f.POSITIVE_Y.getDegreesQuaternion(-entityIn.getYaw());
            quaternion.hamiltonProduct(quaternion1);
            quaternion.hamiltonProduct(quaternion2);
            PTS_modelViewStack.multiply(quaternion);
            RenderSystem.setShader(GameRenderer::getRenderTypeEntityCutoutNoNullShader);
            model.model.Render(entityIn, entityYaw, 0.0f, new Vec3f(0.0f,0.0f,0.0f), PTS_modelViewStack, packedLightIn);
            PTS_modelViewStack.pop();
        }else{
            matrixStackIn.scale(size[0], size[0], size[0]);
            RenderSystem.setShader(GameRenderer::getRenderTypeEntityCutoutNoNullShader);
            model.model.Render(entityIn, bodyYaw, bodyPitch, entityTrans, matrixStackIn, packedLightIn);
        }
        matrixStackIn.pop();
    }

    float[] sizeOfModel(MMDModelManager.Model model){
        float[] size = new float[2];
        size[0] = (model.properties.getProperty("size") == null) ? 1.0f : Float.valueOf(model.properties.getProperty("size"));
        size[1] = (model.properties.getProperty("size_in_inventory") == null) ? 1.0f : Float.valueOf(model.properties.getProperty("size_in_inventory"));
        return size;
    }

    void AnimStateChangeOnce(MMDModelManager.ModelWithEntityData model, MMDModelManager.EntityData.EntityState targetState, Integer layer) {
        String Property = MMDModelManager.EntityData.stateProperty.get(targetState);
        if (model.entityData.stateLayers[layer] != targetState) {
            model.entityData.stateLayers[layer] = targetState;
            model.model.ChangeAnim(MMDAnimManager.GetAnimModel(model.model, Property), layer);
        }
    }

    @Override
    public Identifier getTexture(T entity) {
        return null;
    }
}
