package com.gmail.bradbouquio.File;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.BiConsumer;

public class AnimationFile {

    private YamlConfiguration yml;
    private File saveFile;
    private Path animFilePath;
    private String pathString;
    private int indexToWrite;

    private AnimationFile(){}

    public static AnimationFile getAnimationFile(String playerName, String animationName){
        AnimationFile animationFile = new AnimationFile();
        animationFile.pathString = "plugins/Animator/animations/" + playerName + "/" + animationName + ".yml";
        animationFile.animFilePath = Paths.get(animationFile.pathString);
        try {
            Files.createDirectories(Paths.get("plugins/Animator/animations/" + playerName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!Files.exists(animationFile.animFilePath)){
            try {
                Files.createFile(animationFile.animFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        animationFile.saveFile = new File(animationFile.pathString);
        animationFile.yml = YamlConfiguration.loadConfiguration(animationFile.saveFile);
        return animationFile;
    }

    public void save(){
        try {
            yml.options().copyDefaults(true);
            yml.save(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFrameToFile(Map<String, String> blocksToPutInFrame) {
        int section = yml.getKeys(false) == null ? 0 : yml.getKeys(false).size();
        yml.createSection(Integer.toString(section));
        indexToWrite = yml.getConfigurationSection(section + "").getKeys(false).size();

        BiConsumer saveMapValuesToFile = (k, v) -> {
            yml.createSection(section + "." + indexToWrite);
            yml.addDefault(section + "." + indexToWrite + ".Material", v);
            yml.addDefault(section + "." + indexToWrite + ".coords", k);
            indexToWrite++;
        };

        blocksToPutInFrame.forEach(saveMapValuesToFile);
        save();
    }

    public YamlConfiguration getYml(){
        return yml;
    }
}
