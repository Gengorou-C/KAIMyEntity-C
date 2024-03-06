package com.kAIS.KAIMyEntity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.joml.Vector3f;

import com.kAIS.KAIMyEntity.register.KAIMyEntityRegisterClient;
import com.kAIS.KAIMyEntity.renderer.MMDAnimManager;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager;
import com.kAIS.KAIMyEntity.renderer.MMDTextureManager;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = KAIMyEntity.MODID)
public class KAIMyEntityClient {
    public static final String gameDirectory = Minecraft.getInstance().gameDirectory.getAbsolutePath();
    public static int usingMMDShader = 0;
    public static boolean reloadProperties = false;
    static final int BUFFER = 512;
    static final long TOOBIG = 0x6400000; // Max size of unzipped data, 100MB
    static final int TOOMANY = 1024; // Max number of files

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        KAIMyEntity.logger.info("KAIMyEntity clientSetup begin...");
        checkKAIMyEntityFolder();
        MMDModelManager.Init();
        MMDTextureManager.Init();
        MMDAnimManager.Init();
        KAIMyEntityRegisterClient.Register();
        KAIMyEntity.logger.info("KAIMyEntity clientSetup successful.");
    }

    private static String validateFilename(String filename, String intendedDir) throws java.io.IOException {
        File f = new File(filename);
        String canonicalPath = f.getCanonicalPath();

        File iD = new File(intendedDir);
        String canonicalID = iD.getCanonicalPath();

        if (canonicalPath.startsWith(canonicalID)) {
            return canonicalPath;
        } else {
            throw new IllegalStateException("File is outside extraction target directory.");
        }
    }

    public static final void unzip(String filename, String targetDir) throws java.io.IOException {
        FileInputStream fis = new FileInputStream(filename);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry;
        int entries = 0;
        long total = 0;
        try {
            while ((entry = zis.getNextEntry()) != null) {
                KAIMyEntity.logger.info("Extracting: " + entry);
                int count;
                byte data[] = new byte[BUFFER];
                // Write the files to the disk, but ensure that the filename is valid,
                // and that the file is not insanely big
                String name = validateFilename(targetDir + entry.getName(), ".");
                File targetFile = new File(name);
                if (entry.isDirectory()) {
                    KAIMyEntity.logger.info("Creating directory " + name);
                    new File(name).mkdir();
                    continue;
                }
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(name);
                BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                while (total + BUFFER <= TOOBIG && (count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                    total += count;
                }
                dest.flush();
                dest.close();
                zis.closeEntry();
                entries++;
                if (entries > TOOMANY) {
                    throw new IllegalStateException("Too many files to unzip.");
                }
                if (total + BUFFER > TOOBIG) {
                    throw new IllegalStateException("File being unzipped is too big.");
                }
            }
        } finally {
            zis.close();
        }
    }

    private static void checkKAIMyEntityFolder() {
        File KAIMyEntityFolder = new File(gameDirectory + "/KAIMyEntity");
        if (!KAIMyEntityFolder.exists()) {
            KAIMyEntity.logger.info("KAIMyEntity folder not found, try download from github!");
            KAIMyEntityFolder.mkdir();
            try {
                FileUtils.copyURLToFile(new URL("https://github.com/Gengorou-C/KAIMyEntity-C/releases/download/requiredFiles/KAIMyEntity.zip"), new File(gameDirectory + "/KAIMyEntity.zip"), 30000, 30000);
            } catch (IOException e) {
                KAIMyEntity.logger.info("Download KAIMyEntity.zip failed!");
            }

            try {
                unzip(gameDirectory + "/KAIMyEntity.zip", gameDirectory + "/KAIMyEntity/");
            } catch (IOException e) {
                KAIMyEntity.logger.info("extract KAIMyEntity.zip failed!");
            }
        }
        return;
    }

    public static String calledFrom(int i){
        StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
        if (steArray.length <= i) {
            return "";
        }
        return steArray[i].getClassName();
    }

    public static Vector3f str2Vec3f(String arg){
        Vector3f vector3f = new Vector3f();
        String[] splittedStr = arg.split(",");
        if (splittedStr.length != 3){
            return new Vector3f(0.0f);
        }
        vector3f.x = Float.valueOf(splittedStr[0]);
        vector3f.y = Float.valueOf(splittedStr[1]);
        vector3f.z = Float.valueOf(splittedStr[2]);
        return vector3f;
    }
}
