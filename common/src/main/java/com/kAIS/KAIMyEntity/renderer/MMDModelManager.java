package com.kAIS.KAIMyEntity.renderer;

import com.kAIS.KAIMyEntity.KAIMyEntityClient;
import com.kAIS.KAIMyEntity.NativeFunc;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;

public class MMDModelManager {
    static final Logger logger = LogManager.getLogger();
    static Map<String, Model> models;
    static String gameDirectory = Minecraft.getInstance().gameDirectory.getAbsolutePath();

    public static void Init() {
        models = new HashMap<>();
        logger.info("MMDModelManager.Init() finished");
    }

    public static IMMDModel LoadModel(String modelName, long layerCount) {
        //Model path
        File modelDir = new File(gameDirectory + "/KAIMyEntity/" + modelName);
        String modelDirStr = modelDir.getAbsolutePath();

        String modelFilenameStr;
        boolean isPMD;
        File pmxModelFilename = new File(modelDir, "model.pmx");
        if (pmxModelFilename.isFile()) {
            modelFilenameStr = pmxModelFilename.getAbsolutePath();
            isPMD = false;
        } else {
            File pmdModelFilename = new File(modelDir, "model.pmd");
            if (pmdModelFilename.isFile()) {
                modelFilenameStr = pmdModelFilename.getAbsolutePath();
                isPMD = true;
            } else {
                return null;
            }
        }
        return MMDModelOpenGL.Create(modelFilenameStr, modelDirStr, isPMD, layerCount);
    }

    public static Model GetModel(String modelName, String uuid) {
        Model model = models.get(modelName + uuid);
        if (model == null) {
            IMMDModel m = LoadModel(modelName, 3);
            if (m == null)
                return null;
            MMDAnimManager.AddModel(m);
            AddModel(modelName + uuid, m, modelName);
            model = models.get(modelName + uuid);
        }
        return model;
    }

    public static Model GetModel(String modelName){
        return GetModel(modelName, "");
    }

    public static void AddModel(String Name, IMMDModel model, String modelName) {
        NativeFunc nf = NativeFunc.GetInst();
        EntityData ed = new EntityData();
        ed.stateLayers = new EntityData.EntityState[3];
        ed.playCustomAnim = false;
        ed.rightHandMat = nf.CreateMat();
        ed.leftHandMat = nf.CreateMat();
        ed.matBuffer = ByteBuffer.allocateDirect(64); //float * 16

        ModelWithEntityData m = new ModelWithEntityData();
        m.entityName = Name;
        m.model = model;
        m.modelName = modelName;
        m.entityData = ed;
        model.ResetPhysics();
        model.ChangeAnim(MMDAnimManager.GetAnimModel(model, "idle"), 0);
        models.put(Name, m);
    }

    public static void ReloadModel() {
        for (Model i : models.values())
            DeleteModel(i);
        models = new HashMap<>();
    }

    static void DeleteModel(Model model) {
        MMDModelOpenGL.Delete((MMDModelOpenGL) model.model);

        //Unregister animation user
        MMDAnimManager.DeleteModel(model.model);
    }

    public static class Model {
        public IMMDModel model;
        String entityName;
        String modelName;
        public Properties properties = new Properties();
        boolean isPropertiesLoaded = false;

        public void loadModelProperties(boolean forceReload){
            if (isPropertiesLoaded && !forceReload)
                return;
            String path2Properties = gameDirectory + "/KAIMyEntity/" + modelName + "/model.properties";
            try {
                InputStream istream = new FileInputStream(path2Properties);
                properties.load(istream);
            } catch (IOException e) {
                logger.warn( "KAIMyEntity/" + modelName + "/model.properties not found" );
            }
            isPropertiesLoaded = true;
            KAIMyEntityClient.reloadProperties = false;
        } 
    }

    public static class ModelWithEntityData extends Model {
        public EntityData entityData;
    }

    public static class EntityData {
        public static HashMap<EntityState, String> stateProperty = new HashMap<>() {{
            put(EntityState.Idle, "idle");
            put(EntityState.Walk, "walk");
            put(EntityState.Sprint, "sprint");
            put(EntityState.Air, "air");
            put(EntityState.OnClimbable, "onClimbable");
            put(EntityState.OnClimbableUp, "onClimbableUp");
            put(EntityState.OnClimbableDown, "onClimbableDown");
            put(EntityState.Swim, "swim");
            put(EntityState.Ride, "ride");
            put(EntityState.Ridden, "ridden");
            put(EntityState.Driven, "driven");
            put(EntityState.Sleep, "sleep");
            put(EntityState.ElytraFly, "elytraFly");
            put(EntityState.Die, "die");
            put(EntityState.SwingRight, "swingRight");
            put(EntityState.SwingLeft, "swingLeft");
            put(EntityState.Sneak, "sneak");
            put(EntityState.OnHorse, "onHorse");
            put(EntityState.Crawl, "crawl");
            put(EntityState.LieDown, "lieDown");
        }};
        public boolean playCustomAnim; //Custom animation played in layer 0.
        public long rightHandMat, leftHandMat;
        public EntityState[] stateLayers;
        ByteBuffer matBuffer;

        public enum EntityState {Idle, Walk, Sprint, Air, OnClimbable, OnClimbableUp, OnClimbableDown, Swim, Ride, Ridden, Driven, Sleep, ElytraFly, Die, SwingRight, SwingLeft, ItemRight, ItemLeft, Sneak, OnHorse, Crawl, LieDown}
    }
}