package com.kAIS.KAIMyEntity.register;

import com.kAIS.KAIMyEntity.KAIMyEntity;
import com.kAIS.KAIMyEntity.KAIMyEntityClient;
import com.kAIS.KAIMyEntity.network.KAIMyEntityNetworkPack;
import com.kAIS.KAIMyEntity.renderer.KAIMyEntityRenderFactory;
import com.kAIS.KAIMyEntity.renderer.KAIMyEntityRendererPlayerHelper;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.client.event.InputEvent;
//import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.EntityType;

import org.lwjgl.glfw.GLFW;

import java.io.File;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class KAIMyEntityRegisterClient {
    static KeyMapping keyCustomAnim1 = new KeyMapping("key.customAnim1", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.title");
    static KeyMapping keyCustomAnim2 = new KeyMapping("key.customAnim2", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.title");
    static KeyMapping keyCustomAnim3 = new KeyMapping("key.customAnim3", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, "key.title");
    static KeyMapping keyCustomAnim4 = new KeyMapping("key.customAnim4", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.title");
    static KeyMapping keyReloadModels = new KeyMapping("key.reloadModels", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.title");
    static KeyMapping keyResetPhysics = new KeyMapping("key.resetPhysics", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.title");
    static KeyMapping keyReloadProperties = new KeyMapping("key.reloadProperties", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.title");
    static KeyMapping keyChangeProgram = new KeyMapping("key.changeProgram", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_KP_0, "key.title");

    public static void Register() {
        Minecraft MCinstance = Minecraft.getInstance();
        RegisterRenderers RR = new RegisterRenderers();
        RegisterKeyMappingsEvent RKE = new RegisterKeyMappingsEvent(MCinstance.options);
        for (KeyMapping i : new KeyMapping[]{keyCustomAnim1, keyCustomAnim2, keyCustomAnim3, keyCustomAnim4, keyReloadModels, keyResetPhysics, keyReloadProperties, keyChangeProgram})
            RKE.register(i);

        File[] modelDirs = new File(MCinstance.gameDirectory, "KAIMyEntity").listFiles();
        if (modelDirs != null) {
            for (File i : modelDirs) {
                if (!i.getName().startsWith("EntityPlayer") && !i.getName().equals("DefaultAnim") && !i.getName().equals("Shader")) {
                    String mcEntityName = i.getName().replace('.', ':');
                    if (EntityType.byString(mcEntityName).isPresent()){
                        RR.registerEntityRenderer(EntityType.byString(mcEntityName).get(), new KAIMyEntityRenderFactory<>(mcEntityName));
                        KAIMyEntity.logger.info(mcEntityName + " is present, rendering it.");
                    }else{
                        KAIMyEntity.logger.warn(mcEntityName + " not present, ignore rendering it!");
                    }
                }
            }
        }
        KAIMyEntity.logger.info("KAIMyEntityRegisterClient.Register() finished.");
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onKeyPressed(InputEvent.Key event) {
        Minecraft MCinstance = Minecraft.getInstance();
        LocalPlayer localPlayer = MCinstance.player;
        if (keyCustomAnim1.isDown()) {
            MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + localPlayer.getName().getString());
            if (m != null) {
                KAIMyEntityRendererPlayerHelper.CustomAnim(localPlayer, "1");
                assert localPlayer != null;
                KAIMyEntityRegisterCommon.channel.sendToServer(new KAIMyEntityNetworkPack(1, localPlayer.getUUID(), 1));
            }
        }
        if (keyCustomAnim2.isDown()) {
            MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + localPlayer.getName().getString());
            if (m != null) {
                KAIMyEntityRendererPlayerHelper.CustomAnim(localPlayer, "2");
                assert localPlayer != null;
                KAIMyEntityRegisterCommon.channel.sendToServer(new KAIMyEntityNetworkPack(1, localPlayer.getUUID(), 2));
            }
        }
        if (keyCustomAnim3.isDown()) {
            MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + localPlayer.getName().getString());
            if (m != null) {
                KAIMyEntityRendererPlayerHelper.CustomAnim(localPlayer, "3");
                assert localPlayer != null;
                KAIMyEntityRegisterCommon.channel.sendToServer(new KAIMyEntityNetworkPack(1, localPlayer.getUUID(), 3));
            }
        }
        if (keyCustomAnim4.isDown()) {
            MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + localPlayer.getName().getString());
            if (m != null) {
                KAIMyEntityRendererPlayerHelper.CustomAnim(localPlayer, "4");
                assert localPlayer != null;
                KAIMyEntityRegisterCommon.channel.sendToServer(new KAIMyEntityNetworkPack(1, localPlayer.getUUID(), 4));
            }
        }
        if (keyReloadModels.isDown()) {
            MMDModelManager.ReloadModel();
        }
        if (keyResetPhysics.isDown()) {
            MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + localPlayer.getName().getString());
            if (m != null) {
                KAIMyEntityRendererPlayerHelper.ResetPhysics(localPlayer);
                assert localPlayer != null;
                KAIMyEntityRegisterCommon.channel.sendToServer(new KAIMyEntityNetworkPack(2, localPlayer.getUUID(), 0));
            }
        }
        if (keyReloadProperties.isDown()) {
            KAIMyEntityClient.reloadProperties = true;
        }
        if (keyChangeProgram.isDown()) {
            KAIMyEntityClient.usingMMDShader = 1 - KAIMyEntityClient.usingMMDShader;
            
            if(KAIMyEntityClient.usingMMDShader == 0)
                MCinstance.gui.getChat().addMessage(Component.literal("Default shader"));
            if(KAIMyEntityClient.usingMMDShader == 1)
                MCinstance.gui.getChat().addMessage(Component.literal("MMDShader"));
        }
    }
    
    /*  for debug
    @SubscribeEvent
	public static void eventHandler(RenderGameOverlayEvent.Pre event) {
		if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			//int w = event.getWindow().getGuiScaledWidth(); 0-428
			//int h = event.getWindow().getGuiScaledHeight(); 0-240
            int i = 0;
			Minecraft.getInstance().font.draw(event.getMatrixStack(), KAIMyEntity.debugStr[i], 1, 20+i*15, -16777089);i+=1;
			Minecraft.getInstance().font.draw(event.getMatrixStack(), KAIMyEntity.debugStr[i], 1, 20+i*15, -16777089);i+=1;
            
            for(int j=0; j<32; j++){
                KAIMyEntity.debugStr[j] = "EMPTY";
            }
		}
	}
    */
}