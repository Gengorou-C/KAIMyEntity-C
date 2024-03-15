package com.kAIS.KAIMyEntity.renderer;

import com.kAIS.KAIMyEntity.KAIMyEntityClient;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class KAIMyEntityRenderer<T extends Entity> extends EntityRenderer<T> {
    protected String modelName;
    protected EntityRendererProvider.Context context;

    public KAIMyEntityRenderer(EntityRendererProvider.Context renderManager, String entityName) {
        super(renderManager);
        this.modelName = entityName.replace(':', '.');
        this.context = renderManager;
    }

    @Override
    public boolean shouldRender(T livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        return super.shouldRender(livingEntityIn, camera, camX, camY, camZ);
    }

    @Override
    public void render(T entityIn, float entityYaw, float tickDelta, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, tickDelta, matrixStackIn, bufferIn, packedLightIn);
        String animName = "";
        float bodyYaw = entityYaw;
        if(entityIn instanceof LivingEntity){
            bodyYaw = Mth.rotLerp(tickDelta, ((LivingEntity)entityIn).yBodyRotO, ((LivingEntity)entityIn).yBodyRot);
        }
        float bodyPitch = 0.0f;
        Vector3f entityTrans = new Vector3f(0.0f, 0.0f, 0.0f);
        MMDModelManager.Model model = MMDModelManager.GetModel(modelName, entityIn.getStringUUID());
        if(model == null){
            return;
        }
        MMDModelManager.ModelWithEntityData mwed = (MMDModelManager.ModelWithEntityData)model;
        model.loadModelProperties(false);
        float[] size = sizeOfModel(model);
        
        matrixStackIn.pushPose();
        if(entityIn instanceof LivingEntity){
            if(((LivingEntity) entityIn).getHealth() <= 0.0F){
                animName = "die";
                AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Die, 0);
            }else if(((LivingEntity) entityIn).isSleeping()){
                animName = "sleep";
                bodyYaw = ((LivingEntity) entityIn).getBedOrientation().toYRot() + 180.0f;
                bodyPitch = model.properties.getProperty("sleepingPitch") == null ? 0.0f : Float.valueOf(model.properties.getProperty("sleepingPitch"));
                entityTrans = model.properties.getProperty("sleepingTrans") == null ? new Vector3f(0.0f, 0.0f, 0.0f) : KAIMyEntityClient.str2Vec3f(model.properties.getProperty("sleepingTrans"));
                AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Sleep, 0);
            }
            if(((LivingEntity) entityIn).isBaby()){
                matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            }
        }
        if(animName == ""){
            if (entityIn.isVehicle() && (entityIn.getX() - entityIn.xo != 0.0f || entityIn.getZ() - entityIn.zo != 0.0f)) {
                animName = "driven";
                AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Driven, 0);
            } else if (entityIn.isVehicle()) {
                animName = "ridden";
                AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Ridden, 0);
            } else if (entityIn.isSwimming()) {
                animName = "swim";
                AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Swim, 0);
            } else if ( (entityIn.getX() - entityIn.xo != 0.0f || entityIn.getZ() - entityIn.zo != 0.0f) && entityIn.getVehicle() == null) {
                animName = "walk";
                AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Walk, 0);
            } else {
                animName = "idle";
                AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Idle, 0);
            }
        }
        if(KAIMyEntityClient.calledFrom(6).contains("Inventory") || KAIMyEntityClient.calledFrom(6).contains("class_490")){ // net.minecraft.class_490 == net.minecraft.client.gui.screen.ingame.InventoryScreen
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            PoseStack PTS_modelViewStack = RenderSystem.getModelViewStack();
            PTS_modelViewStack.translate(0.0f, 0.0f, 1000.0f);
            PTS_modelViewStack.pushPose();
            PTS_modelViewStack.scale(20.0f,20.0f, 20.0f);
            PTS_modelViewStack.scale(size[1], size[1], size[1]);
            Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0f);
            Quaternion quaternion1 = Vector3f.XP.rotationDegrees(-entityIn.getXRot());
            Quaternion quaternion2 = Vector3f.YP.rotationDegrees(-entityIn.getYRot());
            quaternion.mul(quaternion1);
            quaternion.mul(quaternion2);
            PTS_modelViewStack.mulPose(quaternion);
            RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
            model.model.Render(entityIn, entityYaw, 0.0f, new Vector3f(0.0f,0.0f,0.0f), tickDelta, PTS_modelViewStack, packedLightIn);
            PTS_modelViewStack.popPose();
        }else{
            matrixStackIn.scale(size[0], size[0], size[0]);
            RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
            model.model.Render(entityIn, bodyYaw, bodyPitch, entityTrans, tickDelta, matrixStackIn, packedLightIn);
        }
        matrixStackIn.popPose();
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
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}
