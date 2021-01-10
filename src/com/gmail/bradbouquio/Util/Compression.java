package com.gmail.bradbouquio.Util;

import com.gmail.bradbouquio.File.AnimationFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Compression {

    public static void compress(AnimationFile animationFile){
        YamlConfiguration yml = animationFile.getYml();
        ConfigurationSection framesSection = yml.getConfigurationSection("frames");

        int numFrames;
        if(framesSection !=null) numFrames = framesSection.getKeys(false).size();
        else return;

        int initialFrameSize = 100;
        if(yml.getConfigurationSection("frames." + 0) != null) initialFrameSize = yml.getConfigurationSection("frames." + 0).getKeys(false).size();
        List<String> initialFrameBlockCoords = new ArrayList<>(initialFrameSize);
        List<String> otherFrameBlockCoords = new ArrayList<>(100);

        Map<String, Object> initialFrameMap = animationFile.getYml().getConfigurationSection("frames." + 0).getValues(true);
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
            otherFrameMaps = yml.getConfigurationSection("frames." + i).getValues(true);
            otherFrameMaps.forEach(createCoordsList2);
        }

        List<String> coordsToRemove = initialFrameBlockCoords.stream()
                .filter(coords->!otherFrameBlockCoords.contains(coords))
                .collect(Collectors.toList());

        for(int i = 0; i < initialFrameSize; i++){
            if(coordsToRemove.contains(yml.getConfigurationSection("frames." + 0).getString(i + ".coords"))){
                yml.getConfigurationSection("frames." + 0).set(String.valueOf(i),null);
            }
        }

        animationFile.save();
    }
}
