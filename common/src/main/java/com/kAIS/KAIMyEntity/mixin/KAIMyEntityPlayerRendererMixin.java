package com.kAIS.KAIMyEntity.mixin;

import com.kAIS.KAIMyEntity.KAIMyEntityClient;
import com.kAIS.KAIMyEntity.NativeFunc;
import com.kAIS.KAIMyEntity.renderer.IMMDModel;
import com.kAIS.KAIMyEntity.renderer.MMDAnimManager;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager.ModelWithEntityData;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class KAIMyEntityPlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public KAIMyEntityPlayerRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = {"render"}, at = @At("HEAD"), cancellable = true)
    public void render(AbstractClientPlayerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider vertexConsumers, int packedLightIn, CallbackInfo ci) {
        MinecraftClient MCinstance = MinecraftClient.getInstance();
        IMMDModel model = null;
        float bodyYaw = entityIn.bodyYaw;
        float bodyPitch = 0.0f;
        Vector3f entityTrans = new Vector3f(0.0f);
        MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + entityIn.getName().getString());
        if (m == null){
            m = MMDModelManager.GetModel("EntityPlayer");
        }
        if (m == null){
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, vertexConsumers, packedLightIn);
            return;
        } 
        if (m != null){
            model = m.model;
        }
        MMDModelManager.ModelWithEntityData mwed = (MMDModelManager.ModelWithEntityData) m;
        mwed.loadModelProperties(KAIMyEntityClient.reloadProperties);
        float sleepingPitch = mwed.properties.getProperty("sleepingPitch") == null ? 0.0f : Float.valueOf(mwed.properties.getProperty("sleepingPitch"));
        Vector3f sleepingTrans = mwed.properties.getProperty("sleepingTrans") == null ? new Vector3f(0.0f) : KAIMyEntityClient.str2Vec3f(mwed.properties.getProperty("sleepingTrans"));
        float flyingPitch = mwed.properties.getProperty("flyingPitch") == null ? 0.0f : Float.valueOf(mwed.properties.getProperty("flyingPitch"));
        Vector3f flyingTrans = mwed.properties.getProperty("flyingTrans") == null ? new Vector3f(0.0f) : KAIMyEntityClient.str2Vec3f(mwed.properties.getProperty("flyingTrans"));
        float swimmingPitch = mwed.properties.getProperty("swimmingPitch") == null ? 0.0f : Float.valueOf(mwed.properties.getProperty("swimmingPitch"));
        Vector3f swimmingTrans = mwed.properties.getProperty("swimmingTrans") == null ? new Vector3f(0.0f) : KAIMyEntityClient.str2Vec3f(mwed.properties.getProperty("swimmingTrans"));
        float crawlingPitch = mwed.properties.getProperty("crawlingPitch") == null ? 0.0f : Float.valueOf(mwed.properties.getProperty("crawlingPitch"));
        Vector3f crawlingTrans = mwed.properties.getProperty("crawlingTrans") == null ? new Vector3f(0.0f) : KAIMyEntityClient.str2Vec3f(mwed.properties.getProperty("crawlingTrans"));
        float[] size = sizeOfModel(mwed);

        if (model != null) {
            if (!mwed.entityData.playCustomAnim) {
                //Layer 0
                if (entityIn.getHealth() == 0.0f) {
                    AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Die, 0);
                } else if (entityIn.isFallFlying()) {
                    AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.ElytraFly, 0);
                    bodyPitch = entityIn.getPitch() + flyingPitch;
                    entityTrans = flyingTrans;
                } else if (entityIn.isSleeping()) {
                    AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Sleep, 0);
                    bodyYaw = entityIn.getSleepingDirection().asRotation() + 180.0f;
                    bodyPitch = sleepingPitch;
                    entityTrans = sleepingTrans;
                } else if (entityIn.hasVehicle()) {
                    if(entityIn.getVehicle().getType() == EntityType.HORSE && (entityIn.getX() - entityIn.prevX != 0.0f || entityIn.getZ() - entityIn.prevZ != 0.0f)){
                        AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.OnHorse, 0);
                        bodyYaw = entityIn.getVehicle().getYaw();
                    }else if(entityIn.getVehicle().getType() == EntityType.HORSE){
                        AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Ride, 0);
                        bodyYaw = entityIn.getVehicle().getYaw();
                    }else{
                        AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Ride, 0);
                    }
                } else if (entityIn.isSwimming()) {
                    AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Swim, 0);
                    bodyPitch = entityIn.getPitch() + swimmingPitch;
                    entityTrans = swimmingTrans;
                } else if (entityIn.isClimbing()) {
                    if(entityIn.getY() - entityIn.prevY > 0){
                        AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.OnClimbableUp, 0);
                    }else if(entityIn.getY() - entityIn.prevY < 0){
                        AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.OnClimbableDown, 0);
                    }else{
                        AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.OnClimbable, 0);
                    }
                } else if (entityIn.isSprinting() && !entityIn.isSneaking()) {
                    AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Sprint, 0);
                } else if (entityIn.isCrawling()){
                    if(entityIn.getX() - entityIn.prevX != 0.0f || entityIn.getZ() - entityIn.prevZ != 0.0f){
                        AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Crawl, 0);
                    }else {
                        AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.LieDown, 0);
                    }
                    bodyPitch = crawlingPitch;
                    entityTrans = crawlingTrans;
                } else if (entityIn.getX() - entityIn.prevX != 0.0f || entityIn.getZ() - entityIn.prevZ != 0.0f) {
                    AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Walk, 0);
                } else {
                    AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Idle, 0);
                }

                //Layer 1
                if(!entityIn.isUsingItem() && !entityIn.handSwinging || entityIn.isSleeping()){
                    if (mwed.entityData.stateLayers[1] != MMDModelManager.EntityData.EntityState.Idle) {
                        mwed.entityData.stateLayers[1] = MMDModelManager.EntityData.EntityState.Idle;
                        model.ChangeAnim(0, 1);
                    }
                }else{
                    if((entityIn.getActiveHand() == Hand.MAIN_HAND) && entityIn.isUsingItem()){
                        String itemId = getItemId_in_ActiveHand(entityIn, Hand.MAIN_HAND);
                        CustomItemActiveAnim(mwed, MMDModelManager.EntityData.EntityState.ItemRight, itemId, "Right", "using", 1);
                    }else if((entityIn.preferredHand == Hand.MAIN_HAND) && entityIn.handSwinging){
                        String itemId = getItemId_in_ActiveHand(entityIn, Hand.MAIN_HAND);
                        CustomItemActiveAnim(mwed, MMDModelManager.EntityData.EntityState.SwingRight, itemId, "Right", "swinging", 1);
                    }else if((entityIn.getActiveHand() == Hand.OFF_HAND) && entityIn.isUsingItem()){
                        String itemId = getItemId_in_ActiveHand(entityIn, Hand.OFF_HAND);
                        CustomItemActiveAnim(mwed, MMDModelManager.EntityData.EntityState.ItemLeft, itemId, "Left", "using", 1);
                    }else if((entityIn.preferredHand == Hand.OFF_HAND) && entityIn.handSwinging){
                        String itemId = getItemId_in_ActiveHand(entityIn, Hand.OFF_HAND);
                        CustomItemActiveAnim(mwed, MMDModelManager.EntityData.EntityState.SwingLeft, itemId, "Left", "swinging", 1);
                    }
                }


                //Layer 2
                if (entityIn.isSneaking() && !entityIn.isCrawling()) {
                    AnimStateChangeOnce(mwed, MMDModelManager.EntityData.EntityState.Sneak, 2);
                } else {
                    if (mwed.entityData.stateLayers[2] != MMDModelManager.EntityData.EntityState.Idle) {
                        mwed.entityData.stateLayers[2] = MMDModelManager.EntityData.EntityState.Idle;
                        model.ChangeAnim(0, 2);
                    }
                }
            }

            if(KAIMyEntityClient.calledFrom(6).contains("InventoryScreen") || KAIMyEntityClient.calledFrom(6).contains("class_490")){ // net.minecraft.class_490 == net.minecraft.client.gui.screen.ingame.InventoryScreen
                RenderSystem.setShader(GameRenderer::getPositionTexProgram);
                MatrixStack PTS_modelViewStack = RenderSystem.getModelViewStack();
                PTS_modelViewStack.push();
                int PosX_in_inventory;
                int PosY_in_inventory;
                if(MCinstance.interactionManager.getCurrentGameMode() != GameMode.CREATIVE){
                    PosX_in_inventory = ((InventoryScreen) MCinstance.currentScreen).getRecipeBookWidget().findLeftEdge(MCinstance.currentScreen.width, 176);
                    PosY_in_inventory = (MCinstance.currentScreen.height - 166) / 2;
                    PTS_modelViewStack.translate(PosX_in_inventory+51, PosY_in_inventory+75, 50.0);
                    PTS_modelViewStack.scale(1.5f, 1.5f, 1.5f);
                }else{
                    PosX_in_inventory = (MCinstance.currentScreen.width - 121) / 2;
                    PosY_in_inventory = (MCinstance.currentScreen.height - 195) / 2;
                    PTS_modelViewStack.translate(PosX_in_inventory+51, PosY_in_inventory+75, 50.0);
                }
                PTS_modelViewStack.scale(size[1], size[1], size[1]);
                PTS_modelViewStack.scale(20.0f,20.0f, -20.0f);
                Quaternionf quaternionf = (new Quaternionf()).rotateZ((float)Math.PI);
                Quaternionf quaternionf1 = (new Quaternionf()).rotateX(-entityIn.getPitch() * ((float)Math.PI / 180F));
                Quaternionf quaternionf2 = (new Quaternionf()).rotateY(-entityIn.bodyYaw * ((float)Math.PI / 180F));
                quaternionf.mul(quaternionf1);
                quaternionf.mul(quaternionf2);
                PTS_modelViewStack.multiply(quaternionf);
                RenderSystem.setShader(GameRenderer::getRenderTypeEntityTranslucentProgram);
                model.Render(entityIn, entityYaw, 0.0f, new Vector3f(0.0f), PTS_modelViewStack, packedLightIn);
                PTS_modelViewStack.pop();
                matrixStackIn.multiply(quaternionf2);
                matrixStackIn.scale(size[1], size[1], size[1]);
                matrixStackIn.scale(0.09f, 0.09f, 0.09f);
            }else{
                matrixStackIn.scale(size[0], size[0], size[0]);
                RenderSystem.setShader(GameRenderer::getRenderTypeEntityTranslucentProgram);
                model.Render(entityIn, bodyYaw, bodyPitch, entityTrans, matrixStackIn, packedLightIn);
            }
            NativeFunc nf = NativeFunc.GetInst();
            float rotationDegree = 0.0f;
            nf.GetRightHandMat(model.GetModelLong(), mwed.entityData.rightHandMat);
            matrixStackIn.push();
            matrixStackIn.peek().getPositionMatrix().mul(DataToMat(nf, mwed.entityData.rightHandMat));
            rotationDegree = ItemRotaionDegree(entityIn, mwed, Hand.MAIN_HAND, "z");
            matrixStackIn.multiply(new Quaternionf().rotateZ(rotationDegree*((float)Math.PI / 180F)));
            rotationDegree = ItemRotaionDegree(entityIn, mwed, Hand.MAIN_HAND, "x");
            matrixStackIn.multiply(new Quaternionf().rotateX(rotationDegree*((float)Math.PI / 180F)));
            matrixStackIn.scale(10.0f, 10.0f, 10.0f);
            MinecraftClient.getInstance().getItemRenderer().renderItem(entityIn, entityIn.getMainHandStack(), ModelTransformationMode.THIRD_PERSON_RIGHT_HAND, false, matrixStackIn, vertexConsumers, entityIn.getWorld(), packedLightIn, OverlayTexture.DEFAULT_UV, 0);
            matrixStackIn.pop();

            nf.GetLeftHandMat(model.GetModelLong(), mwed.entityData.leftHandMat);
            matrixStackIn.push();
            matrixStackIn.peek().getPositionMatrix().mul(DataToMat(nf, mwed.entityData.leftHandMat));
            rotationDegree = ItemRotaionDegree(entityIn, mwed, Hand.OFF_HAND, "z");
            matrixStackIn.multiply(new Quaternionf().rotateZ(rotationDegree*((float)Math.PI / 180F)));
            rotationDegree = ItemRotaionDegree(entityIn, mwed, Hand.OFF_HAND, "x");
            matrixStackIn.multiply(new Quaternionf().rotateX(rotationDegree*((float)Math.PI / 180F)));
            matrixStackIn.scale(10.0f, 10.0f, 10.0f);
            MinecraftClient.getInstance().getItemRenderer().renderItem(entityIn, entityIn.getOffHandStack(), ModelTransformationMode.THIRD_PERSON_LEFT_HAND, true, matrixStackIn, vertexConsumers, entityIn.getWorld(), packedLightIn, OverlayTexture.DEFAULT_UV, 0);
            matrixStackIn.pop();
        }
        ci.cancel();//Added By FMyuchuan. | 隐藏模型脚下的史蒂夫
    }

    String getItemId_in_ActiveHand(AbstractClientPlayerEntity entityIn, Hand hand) {
        String descriptionId = entityIn.getStackInHand(hand).getItem().getTranslationKey();
        String result = descriptionId.substring(descriptionId.indexOf(".") + 1);
        return result;
    }

    void AnimStateChangeOnce(MMDModelManager.ModelWithEntityData model, MMDModelManager.EntityData.EntityState targetState, Integer layer) {
        String Property = MMDModelManager.EntityData.stateProperty.get(targetState);
        if (model.entityData.stateLayers[layer] != targetState) {
            model.entityData.stateLayers[layer] = targetState;
            model.model.ChangeAnim(MMDAnimManager.GetAnimModel(model.model, Property), layer);
        }
    }

    void CustomItemActiveAnim(MMDModelManager.ModelWithEntityData model, MMDModelManager.EntityData.EntityState targetState, String itemName, String activeHand, String handState, Integer layer) {
        long anim = MMDAnimManager.GetAnimModel(model.model, String.format("itemActive_%s_%s_%s", itemName, activeHand, handState));
        if (anim != 0) {
            if (model.entityData.stateLayers[layer] != targetState) {
                model.entityData.stateLayers[layer] = targetState;
                model.model.ChangeAnim(anim, layer);
            }
            return;
        }
        if (targetState == MMDModelManager.EntityData.EntityState.ItemRight || targetState == MMDModelManager.EntityData.EntityState.SwingRight) {
            AnimStateChangeOnce(model, MMDModelManager.EntityData.EntityState.SwingRight, layer);
        } else if (targetState == MMDModelManager.EntityData.EntityState.ItemLeft || targetState == MMDModelManager.EntityData.EntityState.SwingLeft) {
            AnimStateChangeOnce(model, MMDModelManager.EntityData.EntityState.SwingLeft, layer);
        }
    }
    
    float DataToFloat(NativeFunc nf, long data, long pos)
    {
        int temp = 0;
        temp |= nf.ReadByte(data, pos) & 0xff;
        temp |= (nf.ReadByte(data, pos + 1) & 0xff) << 8;
        temp |= (nf.ReadByte(data, pos + 2) & 0xff) << 16;
        temp |= (nf.ReadByte(data, pos + 3) & 0xff) << 24;
        return Float.intBitsToFloat(temp);
    }
    Matrix4f DataToMat(NativeFunc nf, long data)
    {
        Matrix4f result = new Matrix4f(
            DataToFloat(nf, data, 0),DataToFloat(nf, data, 16),DataToFloat(nf, data, 32),DataToFloat(nf, data, 48),
            DataToFloat(nf, data, 4),DataToFloat(nf, data, 20),DataToFloat(nf, data, 36),DataToFloat(nf, data, 52),
            DataToFloat(nf, data, 8),DataToFloat(nf, data, 24),DataToFloat(nf, data, 40),DataToFloat(nf, data, 56),
            DataToFloat(nf, data, 12),DataToFloat(nf, data, 28),DataToFloat(nf, data, 44),DataToFloat(nf, data, 60)
        );
        result.transpose();
        return result;
    }

    float ItemRotaionDegree(AbstractClientPlayerEntity entityIn, ModelWithEntityData mwed, Hand iHand, String axis){
        float result = 0.0f;
        String itemId;
        String strHand;
        String handState;
        
        itemId = getItemId_in_ActiveHand(entityIn,iHand);

        if (iHand == Hand.MAIN_HAND){
            strHand = "Right";
        } else {
            strHand = "Left";
        }

        if ((iHand == entityIn.getActiveHand()) && (entityIn.isUsingItem())){
            handState = "using";
        } else if ((iHand == entityIn.preferredHand) && (entityIn.handSwinging)){
            handState = "swinging";
        } else {
            handState = "idle";
        }

        if (mwed.properties.getProperty(itemId + "_" + strHand + "_" + handState + "_" + axis) != null ){
            result = Float.valueOf(mwed.properties.getProperty(itemId + "_" + strHand + "_" + handState + "_" + axis));
        } else if (mwed.properties.getProperty("default_" + axis) != null){
            result = Float.valueOf(mwed.properties.getProperty("default_" + axis));
        }
        
        return result;
    }

    float[] sizeOfModel(ModelWithEntityData mwed){
        float[] size = new float[2];
        size[0] = (mwed.properties.getProperty("size") == null) ? 1.0f : Float.valueOf(mwed.properties.getProperty("size"));
        size[1] = (mwed.properties.getProperty("size_in_inventory") == null) ? 1.0f : Float.valueOf(mwed.properties.getProperty("size_in_inventory"));
        return size;
    }
}
