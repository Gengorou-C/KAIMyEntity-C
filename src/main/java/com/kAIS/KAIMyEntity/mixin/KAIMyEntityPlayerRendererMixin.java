package com.kAIS.KAIMyEntity.mixin;

import com.kAIS.KAIMyEntity.KAIMyEntityClient;
import com.kAIS.KAIMyEntity.NativeFunc;
import com.kAIS.KAIMyEntity.renderer.IMMDModel;
import com.kAIS.KAIMyEntity.renderer.MMDAnimManager;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager.ModelWithPlayerData;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.GameMode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;

@Mixin(PlayerEntityRenderer.class)
public abstract class KAIMyEntityPlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public KAIMyEntityPlayerRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = {"render"}, at = @At("HEAD"), cancellable = true)
    public void render(AbstractClientPlayerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider vertexConsumers, int packedLightIn, CallbackInfo ci) {
        IMMDModel model = null;
        float bodyYaw = entityIn.bodyYaw;
        float bodyPitch = 0.0f;
        Vec3f entityTrans = new Vec3f(0.0f, 0.0f, 0.0f);
        MMDModelManager.Model m = MMDModelManager.GetPlayerModel("EntityPlayer_" + entityIn.getName().getString());
        if (m == null)
            m = MMDModelManager.GetPlayerModel("EntityPlayer");
        if (m == null){
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, vertexConsumers, packedLightIn);
            return;
        } 
        if (m != null)
            model = m.model;

        MMDModelManager.ModelWithPlayerData mwpd = (MMDModelManager.ModelWithPlayerData) m;
        mwpd.loadModelProperties(KAIMyEntityClient.reloadProperties);
        float sleepingPitch = mwpd.properties.getProperty("sleepingPitch") == null ? 0.0f : Float.valueOf(mwpd.properties.getProperty("sleepingPitch"));
        Vec3f sleepingTrans = mwpd.properties.getProperty("sleepingTrans") == null ? new Vec3f(0.0f, 0.0f, 0.0f) : KAIMyEntityClient.str2Vec3f(mwpd.properties.getProperty("sleepingTrans"));
        float flyingPitch = mwpd.properties.getProperty("flyingPitch") == null ? 0.0f : Float.valueOf(mwpd.properties.getProperty("flyingPitch"));
        Vec3f flyingTrans = mwpd.properties.getProperty("flyingTrans") == null ? new Vec3f(0.0f, 0.0f, 0.0f) : KAIMyEntityClient.str2Vec3f(mwpd.properties.getProperty("flyingTrans"));
        float swimmingPitch = mwpd.properties.getProperty("swimmingPitch") == null ? 0.0f : Float.valueOf(mwpd.properties.getProperty("swimmingPitch"));
        Vec3f swimmingTrans = mwpd.properties.getProperty("swimmingTrans") == null ? new Vec3f(0.0f, 0.0f, 0.0f) : KAIMyEntityClient.str2Vec3f(mwpd.properties.getProperty("swimmingTrans"));

        if (model != null) {
            if (!mwpd.playerData.playCustomAnim) {
                //Layer 0
                if (entityIn.getHealth() == 0.0f) {
                    AnimStateChangeOnce(mwpd, MMDModelManager.PlayerData.EntityState.Die, 0);
                } else if (entityIn.isFallFlying()) {
                    AnimStateChangeOnce(mwpd, MMDModelManager.PlayerData.EntityState.ElytraFly, 0);
                    bodyPitch = entityIn.getPitch() + flyingPitch;
                    entityTrans = flyingTrans;
                } else if (entityIn.isSleeping()) {
                    AnimStateChangeOnce(mwpd, MMDModelManager.PlayerData.EntityState.Sleep, 0);
                    bodyYaw = entityIn.getSleepingDirection().asRotation() + 180.0f;
                    bodyPitch = sleepingPitch;
                    entityTrans = sleepingTrans;
                } else if (entityIn.hasVehicle()) {
                    if(entityIn.getVehicle().getType() == EntityType.HORSE && (entityIn.getX() - entityIn.prevX != 0.0f || entityIn.getZ() - entityIn.prevZ != 0.0f)){
                        AnimStateChangeOnce(mwpd, MMDModelManager.PlayerData.EntityState.OnHorse, 0);
                        bodyYaw = entityIn.getVehicle().getYaw();
                    }else if(entityIn.getVehicle().getType() == EntityType.HORSE){
                        AnimStateChangeOnce(mwpd, MMDModelManager.PlayerData.EntityState.Ride, 0);
                        bodyYaw = entityIn.getVehicle().getYaw();
                    }else{
                        AnimStateChangeOnce(mwpd, MMDModelManager.PlayerData.EntityState.Ride, 0);
                    }
                } else if (entityIn.isSwimming()) {
                    AnimStateChangeOnce(mwpd, MMDModelManager.PlayerData.EntityState.Swim, 0);
                    bodyPitch = entityIn.getPitch() + swimmingPitch;
                    entityTrans = swimmingTrans;
                } else if (entityIn.isClimbing()) {
                    if(entityIn.getY() - entityIn.prevY > 0){
                        AnimStateChangeOnce(mwpd, MMDModelManager.PlayerData.EntityState.OnClimbableUp, 0);
                    }else if(entityIn.getY() - entityIn.prevY < 0){
                        AnimStateChangeOnce(mwpd, MMDModelManager.PlayerData.EntityState.OnClimbableDown, 0);
                    }else{
                        AnimStateChangeOnce(mwpd, MMDModelManager.PlayerData.EntityState.OnClimbable, 0);
                    }
                } else if (entityIn.isSprinting() && !entityIn.isSneaking()) {
                    AnimStateChangeOnce(mwpd, MMDModelManager.PlayerData.EntityState.Sprint, 0);
                } else if (entityIn.getX() - entityIn.prevX != 0.0f || entityIn.getZ() - entityIn.prevZ != 0.0f) {
                    AnimStateChangeOnce(mwpd, MMDModelManager.PlayerData.EntityState.Walk, 0);
                } else {
                    AnimStateChangeOnce(mwpd, MMDModelManager.PlayerData.EntityState.Idle, 0);
                }

                //Layer 1                
                if(!entityIn.isUsingItem() && !entityIn.handSwinging || entityIn.isSleeping()){
                    if (mwpd.playerData.stateLayers[1] != MMDModelManager.PlayerData.EntityState.Idle) {
                        mwpd.playerData.stateLayers[1] = MMDModelManager.PlayerData.EntityState.Idle;
                        model.ChangeAnim(0, 1);
                    }
                }else{
                    if((entityIn.getActiveHand() == Hand.MAIN_HAND) && entityIn.isUsingItem()){
                        String itemId = getItemId_in_ActiveHand(entityIn, Hand.MAIN_HAND);
                        CustomItemActiveAnim(mwpd, MMDModelManager.PlayerData.EntityState.ItemRight, itemId, "Right", "using", 1);
                    }else if((entityIn.preferredHand == Hand.MAIN_HAND) && entityIn.handSwinging){
                        String itemId = getItemId_in_ActiveHand(entityIn, Hand.MAIN_HAND);
                        CustomItemActiveAnim(mwpd, MMDModelManager.PlayerData.EntityState.SwingRight, itemId, "Right", "swinging", 1);
                    }else if((entityIn.getActiveHand() == Hand.OFF_HAND) && entityIn.isUsingItem()){
                        String itemId = getItemId_in_ActiveHand(entityIn, Hand.OFF_HAND);
                        CustomItemActiveAnim(mwpd, MMDModelManager.PlayerData.EntityState.ItemLeft, itemId, "Left", "using", 1);
                    }else if((entityIn.preferredHand == Hand.OFF_HAND) && entityIn.handSwinging){
                        String itemId = getItemId_in_ActiveHand(entityIn, Hand.OFF_HAND);
                        CustomItemActiveAnim(mwpd, MMDModelManager.PlayerData.EntityState.SwingLeft, itemId, "Left", "swinging", 1);
                    }
                }

                //Layer 2
                if (entityIn.isSneaking()) {
                    AnimStateChangeOnce(mwpd, MMDModelManager.PlayerData.EntityState.Sneak, 2);
                } else {
                    if (mwpd.playerData.stateLayers[2] != MMDModelManager.PlayerData.EntityState.Idle) {
                        mwpd.playerData.stateLayers[2] = MMDModelManager.PlayerData.EntityState.Idle;
                        model.ChangeAnim(0, 2);
                    }
                }
            }

            float[] size = sizeOfModel(mwpd);
            if(KAIMyEntityClient.reloadProperties)
                KAIMyEntityClient.reloadProperties = false;
            if(KAIMyEntityClient.calledFrom(6).contains("Inventory") || KAIMyEntityClient.calledFrom(6).contains("class_490")){ // net.minecraft.class_490 == net.minecraft.client.gui.screen.ingame.InventoryScreen
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                MatrixStack PTS_modelViewStack = RenderSystem.getModelViewStack();
                PTS_modelViewStack.scale(size[1], size[1], size[1]);
                PTS_modelViewStack.translate(0.0f, 0.0f, 1000.0f);
                PTS_modelViewStack.push();
                PTS_modelViewStack.scale(20.0f,20.0f, 20.0f);
                if(MinecraftClient.getInstance().interactionManager.getCurrentGameMode() != GameMode.CREATIVE)
                    PTS_modelViewStack.scale(1.5f, 1.5f, 1.5f);
                    Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0f);
                    Quaternion quaternion1 = Vec3f.POSITIVE_X.getDegreesQuaternion(-entityIn.getPitch());
                    Quaternion quaternion2 = Vec3f.POSITIVE_Y.getDegreesQuaternion(-entityIn.bodyYaw);
                quaternion.hamiltonProduct(quaternion1);
                quaternion.hamiltonProduct(quaternion2);
                PTS_modelViewStack.multiply(quaternion);
                RenderSystem.setShader(GameRenderer::getRenderTypeEntityTranslucentShader);
                model.Render(entityIn, entityYaw, 0.0f, new Vec3f(0.0f,0.0f,0.0f), PTS_modelViewStack, packedLightIn);
                PTS_modelViewStack.pop();
                matrixStackIn.multiply(quaternion2);
                matrixStackIn.scale(size[1], size[1], size[1]);
                matrixStackIn.scale(0.09f, 0.09f, 0.09f);
            }else{
                matrixStackIn.scale(size[0], size[0], size[0]);
                RenderSystem.setShader(GameRenderer::getRenderTypeEntityTranslucentShader);
                model.Render(entityIn, bodyYaw, bodyPitch, entityTrans, matrixStackIn, packedLightIn);
            }
            NativeFunc nf = NativeFunc.GetInst();
            float rotationDegree = 0.0f;
            nf.GetRightHandMat(model.GetModelLong(), mwpd.playerData.rightHandMat);
            matrixStackIn.push();
            matrixStackIn.peek().getPositionMatrix().multiply(DataToMat(nf, mwpd.playerData.rightHandMat));
            rotationDegree = ItemRotaionDegree(entityIn, mwpd, Hand.MAIN_HAND, "z");
            matrixStackIn.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationDegree));
            rotationDegree = ItemRotaionDegree(entityIn, mwpd, Hand.MAIN_HAND, "x");
            matrixStackIn.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotationDegree));
            matrixStackIn.scale(10.0f, 10.0f, 10.0f);
            MinecraftClient.getInstance().getItemRenderer().renderItem(entityIn, entityIn.getMainHandStack(), ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, false, matrixStackIn, vertexConsumers, entityIn.world, packedLightIn, OverlayTexture.DEFAULT_UV, 0);
            matrixStackIn.pop();

            nf.GetLeftHandMat(model.GetModelLong(), mwpd.playerData.leftHandMat);
            matrixStackIn.push();
            matrixStackIn.peek().getPositionMatrix().multiply(DataToMat(nf, mwpd.playerData.leftHandMat));
            rotationDegree = ItemRotaionDegree(entityIn, mwpd, Hand.OFF_HAND, "z");
            matrixStackIn.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationDegree));
            rotationDegree = ItemRotaionDegree(entityIn, mwpd, Hand.OFF_HAND, "x");
            matrixStackIn.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotationDegree));
            matrixStackIn.scale(10.0f, 10.0f, 10.0f);
            MinecraftClient.getInstance().getItemRenderer().renderItem(entityIn, entityIn.getOffHandStack(), ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, true, matrixStackIn, vertexConsumers, entityIn.world, packedLightIn, OverlayTexture.DEFAULT_UV, 0);
            matrixStackIn.pop();
        }
        ci.cancel();//Added By FMyuchuan. | 隐藏模型脚下的史蒂夫
    }

    String getItemId_in_ActiveHand(AbstractClientPlayerEntity entityIn, Hand hand) {
        String descriptionId = entityIn.getStackInHand(hand).getItem().getTranslationKey();
        String result = descriptionId.substring(descriptionId.indexOf(".") + 1);
        return result;
    }

    void AnimStateChangeOnce(MMDModelManager.ModelWithPlayerData model, MMDModelManager.PlayerData.EntityState targetState, Integer layer) {
        String Property = MMDModelManager.PlayerData.stateProperty.get(targetState);
        if (model.playerData.stateLayers[layer] != targetState) {
            model.playerData.stateLayers[layer] = targetState;
            model.model.ChangeAnim(MMDAnimManager.GetAnimModel(model.model, Property), layer);
        }
    }

    void CustomItemActiveAnim(MMDModelManager.ModelWithPlayerData model, MMDModelManager.PlayerData.EntityState targetState, String itemName, String activeHand, String handState, Integer layer) {
        long anim = MMDAnimManager.GetAnimModel(model.model, String.format("itemActive_%s_%s_%s", itemName, activeHand, handState));
        if (anim != 0) {
            if (model.playerData.stateLayers[layer] != targetState) {
                model.playerData.stateLayers[layer] = targetState;
                model.model.ChangeAnim(anim, layer);
            }
            return;
        }
        if (targetState == MMDModelManager.PlayerData.EntityState.ItemRight || targetState == MMDModelManager.PlayerData.EntityState.SwingRight) {
            AnimStateChangeOnce(model, MMDModelManager.PlayerData.EntityState.SwingRight, layer);
        } else if (targetState == MMDModelManager.PlayerData.EntityState.ItemLeft || targetState == MMDModelManager.PlayerData.EntityState.SwingLeft) {
            AnimStateChangeOnce(model, MMDModelManager.PlayerData.EntityState.SwingLeft, layer);
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
        Matrix4f result = new Matrix4f();
        result.readRowMajor(FloatBuffer.wrap(new float[]
        {
            DataToFloat(nf, data, 0),
            DataToFloat(nf, data, 4),
            DataToFloat(nf, data, 8),
            DataToFloat(nf, data, 12),
            DataToFloat(nf, data, 16),
            DataToFloat(nf, data, 20),
            DataToFloat(nf, data, 24),
            DataToFloat(nf, data, 28),
            DataToFloat(nf, data, 32),
            DataToFloat(nf, data, 36),
            DataToFloat(nf, data, 40),
            DataToFloat(nf, data, 44),
            DataToFloat(nf, data, 48),
            DataToFloat(nf, data, 52),
            DataToFloat(nf, data, 56),
            DataToFloat(nf, data, 60),
        }));
        result.transpose();
        return result;
    }

    float ItemRotaionDegree(AbstractClientPlayerEntity entityIn, ModelWithPlayerData mwpd, Hand iHand, String axis){
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

        if (mwpd.properties.getProperty(itemId + "_" + strHand + "_" + handState + "_" + axis) != null ){
            result = Float.valueOf(mwpd.properties.getProperty(itemId + "_" + strHand + "_" + handState + "_" + axis));
        } else if (mwpd.properties.getProperty("default_" + axis) != null){
            result = Float.valueOf(mwpd.properties.getProperty("default_" + axis));
        }
        
        return result;
    }

    float[] sizeOfModel(ModelWithPlayerData mwpd){
        float[] size = new float[2];
        size[0] = (mwpd.properties.getProperty("size") == null) ? 1.0f : Float.valueOf(mwpd.properties.getProperty("size"));
        size[1] = (mwpd.properties.getProperty("size_in_inventory") == null) ? 1.0f : Float.valueOf(mwpd.properties.getProperty("size_in_inventory"));
        return size;
    }
}
