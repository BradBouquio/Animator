package com.gmail.bradbouquio.Util;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

/*
    FinalFrameBlocksMapper reads a yml configuration of an animation data file
    and integrates the data into one frame. Starting with the last frame it
    collects information about each Location, and moving backwards in time it
    integrates only Location information about blocks that have never changed.
    This provides the Animation class with a fast way to compare the final
    frame with the new frame it's building.
 */

public class FinalFrameBlocksMapper {

    public static Map<String, String> lastFramedBlockMaterials(YamlConfiguration yml, World world, Integer indexOfFinalFrame){
        int initialFrameSize = 100;
        if(yml.getConfigurationSection(String.valueOf(0)) != null) initialFrameSize = yml.getConfigurationSection(String.valueOf(0)).getKeys(false).size();
        Map<String, String> lastFramedBlockMaterials = new HashMap<>(initialFrameSize);


        for(int i = indexOfFinalFrame; i >= 0; i--){
            int numBlocksOfFrame = yml.getConfigurationSection(String.valueOf(i)).getKeys(false).size();
            for(int j = 0; j < numBlocksOfFrame; j++){
                String coords = yml.getString(i + "." + j + ".coords");
                String material = yml.getString(i + "." + j + ".Material");
                lastFramedBlockMaterials.putIfAbsent(coords, material);
            }
        }

        return lastFramedBlockMaterials;
    }
}
