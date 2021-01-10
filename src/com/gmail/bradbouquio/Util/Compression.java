package com.gmail.bradbouquio.Util;

import com.gmail.bradbouquio.File.AnimationFile;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Compression {

    public static void compress(AnimationFile animationFile){
        YamlConfiguration yml = animationFile.getYml();
        int numFrames = animationFile.getYml().getKeys(false).size();
        int initialFrameSize = 100;
        if(animationFile.getYml().getConfigurationSection(String.valueOf(0)) != null) initialFrameSize = animationFile.getYml().getConfigurationSection(String.valueOf(0)).getKeys(false).size();
        List<String> initialFrameBlockCoords = new ArrayList<>(initialFrameSize);
        List<String> otherFrameBlockCoords = new ArrayList<>(100);

        Map<String, Object> initialFrameMap = animationFile.getYml().getConfigurationSection(String.valueOf(0)).getValues(true);
        Map<String, Object> otherFrameMaps;


        BiConsumer createCoordsList = (k,v) -> {
            if(((String)k).endsWith(".coords")){
                initialFrameBlockCoords.add((String)v);
            }
        };
        BiConsumer createCoordsList2 = (k,v) -> {
            if(((String)k).endsWith(".coords")){
                otherFrameBlockCoords.add((String)v);
            }
        };

        initialFrameMap.forEach(createCoordsList);

        for(int i = 1; i < numFrames; i++){
            otherFrameMaps = animationFile.getYml().getConfigurationSection(String.valueOf(i)).getValues(true);
            otherFrameMaps.forEach(createCoordsList2);
        }

        List<String> coordsToRemove = initialFrameBlockCoords.stream()
                .filter(coords->!otherFrameBlockCoords.contains(coords))
                .collect(Collectors.toList());

        for(int i = 0; i < initialFrameSize; i++){
            if(coordsToRemove.contains(yml.getConfigurationSection(String.valueOf(0)).getString(i + ".coords"))){
                yml.getConfigurationSection(String.valueOf(0)).set(String.valueOf(i),null);
            }
        }

        animationFile.save();
    }
}
