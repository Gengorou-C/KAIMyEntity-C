package com.kAIS.KAIMyEntity;

import com.kAIS.KAIMyEntity.register.KAIMyEntityRegisterClient;
import com.kAIS.KAIMyEntity.renderer.MMDAnimManager;
import com.kAIS.KAIMyEntity.renderer.MMDModelManager;
import com.kAIS.KAIMyEntity.renderer.MMDTextureManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KAIMyEntityClient implements ClientModInitializer {
    public static final Logger logger = LogManager.getLogger();
    public static int usingMMDShader = 0;
    public static boolean reloadProperties = false;
    static String gameDirectory = MinecraftClient.getInstance().runDirectory.getAbsolutePath();
    static final int BUFFER = 512;
    static final long TOOBIG = 0x6400000; // Max size of unzipped data, 100MB
    static final int TOOMANY = 1024;      // Max number of files

    @Override
    public void onInitializeClient() {
        logger.info("KAIMyEntity Init begin...");
        checkKAIMyEntityFolder();
        MMDModelManager.Init();
        logger.info("MMDModelManager.Init() finished");
        MMDTextureManager.Init();
        logger.info("MMDTextureManager.Init() finished");
        MMDAnimManager.Init();
        logger.info("MMDAnimManager.Init() finished");
        KAIMyEntityRegisterClient.Register();
        logger.info("KAIMyEntityRegisterClient.Register() finished");
        logger.info("KAIMyEntity preInit successful.");
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
                logger.info("Extracting: " + entry);
                int count;
                byte data[] = new byte[BUFFER];
                // Write the files to the disk, but ensure that the filename is valid,
                // and that the file is not insanely big
                String name = validateFilename(targetDir+entry.getName(), ".");
                File targetFile = new File(name);
                if (entry.isDirectory()) {
                    logger.info("Creating directory " + name);
                    new File(name).mkdir();
                    continue;
                }
                if (!targetFile.getParentFile().exists()){
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

    private void checkKAIMyEntityFolder(){
        File KAIMyEntityFolder = new File(gameDirectory + "/KAIMyEntity");
        if (!KAIMyEntityFolder.exists()){
            logger.info("KAIMyEntity folder not found, try download from github!");
            KAIMyEntityFolder.mkdir();
            try{
                FileUtils.copyURLToFile(new URL("https://github.com/Gengorou-C/KAIMyEntity-C/releases/download/requiredFiles/KAIMyEntity.zip"), new File(gameDirectory + "/KAIMyEntity.zip"), 30000, 30000);
            }catch (IOException e){
                logger.info("Download KAIMyEntity.zip failed!");
            }

            try{
                unzip(gameDirectory + "/KAIMyEntity.zip", gameDirectory + "/KAIMyEntity/");
            }catch (IOException e){
                logger.info("extract KAIMyEntity.zip failed!");
            }
        }
        return;
    }
}
