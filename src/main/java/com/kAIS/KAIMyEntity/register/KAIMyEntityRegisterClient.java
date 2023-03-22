package com.kAIS.KAIMyEntity.register;

import com.kAIS.KAIMyEntity.KAIMyEntityClient;
import com.kAIS.KAIMyEntity.network.KAIMyEntityNetworkPack;
import com.kAIS.KAIMyEntity.renderer.KAIMyEntityRenderFactory;
import com.kAIS.KAIMyEntity.renderer.KAIMyEntityRendererPlayerHelper;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class KAIMyEntityRegisterClient {
    static KeyBinding keyResetPhysics = new KeyBinding("key.resetPhysics", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.title");
    static KeyBinding keyReloadModels = new KeyBinding("key.reloadModels", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.title");
    static KeyBinding keyReloadProperties = new KeyBinding("key.reloadProperties", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.title");
    static KeyBinding keyChangeProgram = new KeyBinding("key.changeProgram", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_0, "key.title");
    static KeyBinding keyCustomAnim1 = new KeyBinding("key.customAnim1", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.title");
    static KeyBinding keyCustomAnim2 = new KeyBinding("key.customAnim2", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.title");
    static KeyBinding keyCustomAnim3 = new KeyBinding("key.customAnim3", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, "key.title");
    static KeyBinding keyCustomAnim4 = new KeyBinding("key.customAnim4", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.title");
    static KeyBinding[] keyBindings = new KeyBinding[]{keyCustomAnim1, keyCustomAnim2, keyCustomAnim3, keyCustomAnim4, keyReloadModels, keyResetPhysics, keyReloadProperties, keyChangeProgram};
    static KeyBinding[] customKeyBindings = new KeyBinding[]{keyCustomAnim1, keyCustomAnim2, keyCustomAnim3, keyCustomAnim4};

    public static void Register() {
        MinecraftClient MCinstance = MinecraftClient.getInstance();
        for (KeyBinding i : keyBindings)
            KeyBindingHelper.registerKeyBinding(i);
        for (int i = 0; i < customKeyBindings.length; i++) {
            int finalI = i;
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                while (customKeyBindings[finalI].wasPressed()) {
                    onCustomKeyDown(finalI + 1);
                }
            });
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyReloadModels.wasPressed()) {
                MMDModelManager.ReloadModel();
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyResetPhysics.wasPressed()) {
                onKeyResetPhysicsDown();
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyReloadProperties.wasPressed()) {
                KAIMyEntityClient.reloadProperties = true;
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyChangeProgram.wasPressed()) {
                KAIMyEntityClient.usingMMDShader = 1 - KAIMyEntityClient.usingMMDShader;
                if(KAIMyEntityClient.usingMMDShader == 0)
                    MCinstance.inGameHud.getChatHud().addMessage(Text.of("Default shader"));
                if(KAIMyEntityClient.usingMMDShader == 1)
                    MCinstance.inGameHud.getChatHud().addMessage(Text.of("MMDShader"));
            }
        });

        File[] modelDirs = new File(MCinstance.runDirectory, "KAIMyEntity").listFiles();
        if (modelDirs != null) {
            for (File i : modelDirs) {
                if (!i.getName().startsWith("EntityPlayer") && !i.getName().equals("DefaultAnim") && !i.getName().equals("Shader")) {
                    String mcEntityName = i.getName().replace('.', ':');
                    if (EntityType.get(mcEntityName).isPresent())
                        EntityRendererRegistry.register(EntityType.get(mcEntityName).get(), new KAIMyEntityRenderFactory<>(mcEntityName));
                    else
                        KAIMyEntityClient.logger.warn(mcEntityName + " not present, ignore rendering it!");
                }
            }
        }
        
        ClientPlayNetworking.registerGlobalReceiver(KAIMyEntityRegisterCommon.KAIMYENTITY_S2C, (client, handler, buf, responseSender) -> {
            int opCode = buf.readInt();
            UUID playerUUID = buf.readUuid();
            int arg0 = buf.readInt();
            client.execute(() -> {
                KAIMyEntityNetworkPack.DoInClient(opCode, playerUUID, arg0);
            });
        });
        
        KAIMyEntityClient.logger.info("KAIMyEntityRegisterClient.Register() finished");
    }

    public static void onKeyResetPhysicsDown() {
        MinecraftClient MCinstance = MinecraftClient.getInstance();
        ClientPlayerEntity localPlayer = MCinstance.player;
        KAIMyEntityNetworkPack.sendToServer(2, localPlayer.getUuid(), 0);
        KAIMyEntityRendererPlayerHelper.ResetPhysics(localPlayer);
    }

    public static void onCustomKeyDown(Integer numOfKey) {
        MinecraftClient MCinstance = MinecraftClient.getInstance();
        ClientPlayerEntity localPlayer = MCinstance.player;
        KAIMyEntityNetworkPack.sendToServer(1, localPlayer.getUuid(), numOfKey);
        KAIMyEntityRendererPlayerHelper.CustomAnim(localPlayer, numOfKey.toString());
    }
}
