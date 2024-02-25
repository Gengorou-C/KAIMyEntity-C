package com.kAIS.KAIMyEntity.renderer;

import com.kAIS.KAIMyEntity.KAIMyEntity;
import com.kAIS.KAIMyEntity.NativeFunc;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class MMDAnimManager {
    static NativeFunc nf;
    static Map<String, Long> animStatic;
    static Map<IMMDModel, Map<String, Long>> animModel;
    static String defaultAnimDir = new File(Minecraft.getInstance().gameDirectory, "KAIMyEntity/DefaultAnim").getAbsolutePath();

    public static void Init() {
        nf = NativeFunc.GetInst();
        animStatic = new HashMap<>();
        animModel = new HashMap<>();
        KAIMyEntity.logger.info("MMDAnimManager.Init() finished");
    }

    public static void AddModel(IMMDModel model) {
        animModel.put(model, new HashMap<>());
    }

    public static void DeleteModel(IMMDModel model) {
        Collection<Long> arr = animModel.get(model).values();
        for (Long i : arr)
            nf.DeleteAnimation(i);
        animModel.remove(model);
    }

    //For other entity (Multiple model)
    public static long GetAnimModel(IMMDModel model, String animName) {
        String defaultVmdFilename = GetAnimationFilename(defaultAnimDir, animName);
        String filename = GetAnimationFilename(model.GetModelDir(), animName);
        Map<String, Long> sub = animModel.get(model);
        Long result = sub.get(filename);
        if (result == null) {
            long anim = nf.LoadAnimation(model.GetModelLong(), filename);
            if (anim == 0)
                anim = nf.LoadAnimation(model.GetModelLong(), defaultVmdFilename);
            if (anim == 0)
                return 0;
            result = anim;
            sub.put(filename, result);
        }
        return result;
    }

    static String GetAnimationFilename(String modelDir, String animName) {
        File animFilename = new File(modelDir, animName + ".vmd");
        return animFilename.getAbsolutePath();
    }
}
