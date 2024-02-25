package com.kAIS.KAIMyEntity.renderer;

import com.kAIS.KAIMyEntity.KAIMyEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
    public void render(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        Minecraft MCinstance = Minecraft.getInstance();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        String animName = "";
        float bodyYaw = entityYaw;
        float bodyPitch = 0.0f;
        Vector3f entityTrans = new Vector3f(0.0f);
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
                entityTrans = model.properties.getProperty("sleepingTrans") == null ? new Vector3f(0.0f) : KAIMyEntity.str2Vec3f(model.properties.getProperty("sleepingTrans"));
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
        if(KAIMyEntity.calledFrom(6).contains("Inventory") || KAIMyEntity.calledFrom(6).contains("class_490")){ // net.minecraft.class_490 == net.minecraft.client.gui.screen.ingame.InventoryScreen
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            PoseStack PTS_modelViewStack = RenderSystem.getModelViewStack();
            int PosX_in_inventory;
            int PosY_in_inventory;
            PosX_in_inventory = (MCinstance.screen.width - 176) / 2;
            PosY_in_inventory = (MCinstance.screen.height - 166) / 2;
            PTS_modelViewStack.translate(PosX_in_inventory+51, PosY_in_inventory+60, 50.0);
            PTS_modelViewStack.pushPose();
            PTS_modelViewStack.scale(20.0f,20.0f, -20.0f);
            PTS_modelViewStack.scale(size[1], size[1], size[1]);
            Quaternionf quaternionf = (new Quaternionf()).rotateZ((float)Math.PI);
            Quaternionf quaternionf1 = (new Quaternionf()).rotateX(-entityIn.getXRot() * ((float)Math.PI / 180F));
            Quaternionf quaternionf2 = (new Quaternionf()).rotateY(-entityIn.getYRot() * ((float)Math.PI / 180F));
            quaternionf.mul(quaternionf1);
            quaternionf.mul(quaternionf2);
            PTS_modelViewStack.mulPose(quaternionf);
            RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
            model.model.Render(entityIn, entityYaw, 0.0f, new Vector3f(0.0f), PTS_modelViewStack, packedLightIn);
            PTS_modelViewStack.popPose();
        }else{
            matrixStackIn.scale(size[0], size[0], size[0]);
            RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
            model.model.Render(entityIn, bodyYaw, bodyPitch, entityTrans, matrixStackIn, packedLightIn);
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
