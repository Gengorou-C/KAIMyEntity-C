package com.kAIS.KAIMyEntity.fabric.register;

import com.kAIS.KAIMyEntity.KAIMyEntityClient;
import com.kAIS.KAIMyEntity.fabric.network.KAIMyEntityNetworkPack;
import com.kAIS.KAIMyEntity.renderer.KAIMyEntityRenderFactory;
import com.kAIS.KAIMyEntity.renderer.KAIMyEntityRendererPlayerHelper;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class KAIMyEntityRegisterClient {
    static KeyMapping keyResetPhysics = new KeyMapping("key.resetPhysics", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.title");
    static KeyMapping keyReloadModels = new KeyMapping("key.reloadModels", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.title");
    static KeyMapping keyReloadProperties = new KeyMapping("key.reloadProperties", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.title");
    static KeyMapping keyChangeProgram = new KeyMapping("key.changeProgram", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_KP_0, "key.title");
    static KeyMapping keyCustomAnim1 = new KeyMapping("key.customAnim1", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.title");
    static KeyMapping keyCustomAnim2 = new KeyMapping("key.customAnim2", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.title");
    static KeyMapping keyCustomAnim3 = new KeyMapping("key.customAnim3", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, "key.title");
    static KeyMapping keyCustomAnim4 = new KeyMapping("key.customAnim4", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.title");
    static KeyMapping[] keyBindings = new KeyMapping[]{keyCustomAnim1, keyCustomAnim2, keyCustomAnim3, keyCustomAnim4, keyReloadModels, keyResetPhysics, keyReloadProperties, keyChangeProgram};
    static KeyMapping[] customKeyBindings = new KeyMapping[]{keyCustomAnim1, keyCustomAnim2, keyCustomAnim3, keyCustomAnim4};

    public static void Register() {
        Minecraft MCinstance = Minecraft.getInstance();
        for (KeyMapping i : keyBindings)
            KeyBindingHelper.registerKeyBinding(i);
        for (int i = 0; i < customKeyBindings.length; i++) {
            int finalI = i;
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                while (customKeyBindings[finalI].consumeClick()) {
                    onCustomKeyDown(finalI + 1);
                }
            });
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyReloadModels.consumeClick()) {
                MMDModelManager.ReloadModel();
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyResetPhysics.consumeClick()) {
                onKeyResetPhysicsDown();
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyReloadProperties.consumeClick()) {
                KAIMyEntityClient.reloadProperties = true;
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyChangeProgram.consumeClick()) {
                KAIMyEntityClient.usingMMDShader = 1 - KAIMyEntityClient.usingMMDShader;
                if(KAIMyEntityClient.usingMMDShader == 0)
                    MCinstance.gui.getChat().addMessage(Component.nullToEmpty("Default shader"));
                if(KAIMyEntityClient.usingMMDShader == 1)
                    MCinstance.gui.getChat().addMessage(Component.nullToEmpty("MMDShader"));
            }
        });

        File[] modelDirs = new File(MCinstance.gameDirectory, "KAIMyEntity").listFiles();
        if (modelDirs != null) {
            for (File i : modelDirs) {
                if (!i.getName().startsWith("EntityPlayer") && !i.getName().equals("DefaultAnim") && !i.getName().equals("Shader")) {
                    String mcEntityName = i.getName().replace('.', ':');
                    if (EntityType.byString(mcEntityName).isPresent())
                        EntityRendererRegistry.register(EntityType.byString(mcEntityName).get(), new KAIMyEntityRenderFactory<>(mcEntityName));
                    else
                        KAIMyEntityClient.logger.warn(mcEntityName + " not present, ignore rendering it!");
                }
            }
        }
        
        ClientPlayNetworking.registerGlobalReceiver(KAIMyEntityRegisterCommon.KAIMYENTITY_S2C, (client, handler, buf, responseSender) -> {
            int opCode = buf.readInt();
            UUID playerUUID = buf.readUUID();
            int arg0 = buf.readInt();
            client.execute(() -> {
                KAIMyEntityNetworkPack.DoInClient(opCode, playerUUID, arg0);
            });
        });
        
        KAIMyEntityClient.logger.info("KAIMyEntityRegisterClient.Register() finished");
    }

    public static void onKeyResetPhysicsDown() {
        Minecraft MCinstance = Minecraft.getInstance();
        LocalPlayer localPlayer = MCinstance.player;
        KAIMyEntityNetworkPack.sendToServer(2, localPlayer.getUUID(), 0);
        KAIMyEntityRendererPlayerHelper.ResetPhysics(localPlayer);
    }

    public static void onCustomKeyDown(Integer numOfKey) {
        Minecraft MCinstance = Minecraft.getInstance();
        LocalPlayer localPlayer = MCinstance.player;
        KAIMyEntityNetworkPack.sendToServer(1, localPlayer.getUUID(), numOfKey);
        KAIMyEntityRendererPlayerHelper.CustomAnim(localPlayer, numOfKey.toString());
    }
}
