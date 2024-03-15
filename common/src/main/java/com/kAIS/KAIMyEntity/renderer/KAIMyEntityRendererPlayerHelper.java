package com.kAIS.KAIMyEntity.renderer;

import net.minecraft.world.entity.player.Player;

public class KAIMyEntityRendererPlayerHelper {

    KAIMyEntityRendererPlayerHelper() {
    }

    public static void ResetPhysics(Player player) {
        MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + player.getName().getString());
        if (m == null)
            m = MMDModelManager.GetModel("EntityPlayer");
        if (m != null) {
            IMMDModel model = m.model;
            ((MMDModelManager.ModelWithEntityData) m).entityData.playCustomAnim = false;
            model.ChangeAnim(MMDAnimManager.GetAnimModel(model, "idle"), 0);
            model.ChangeAnim(0, 1);
            model.ChangeAnim(0, 2);
            model.ResetPhysics();
        }
    }

    public static void CustomAnim(Player player, String id) {
        MMDModelManager.Model m = MMDModelManager.GetModel("EntityPlayer_" + player.getName().getString());
        if (m == null)
            m = MMDModelManager.GetModel("EntityPlayer");
        if (m != null) {
            MMDModelManager.ModelWithEntityData mwed = (MMDModelManager.ModelWithEntityData) m;
            IMMDModel model = m.model;
            mwed.entityData.playCustomAnim = true;
            model.ChangeAnim(MMDAnimManager.GetAnimModel(model, "custom_" + id), 0);
            model.ChangeAnim(0, 1);
            model.ChangeAnim(0, 2);
        }
    }
}