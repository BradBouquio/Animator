package com.gmail.bradbouquio.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class CompareBlockMaps {

    public static Map<String, String> compare(Map<String, String> selectionMap, Map<String, String> finalFrameMap){
        Map<String, String> changedOrMissingBlocks = new HashMap<>(selectionMap.size());

        BiConsumer getChangedOrMissing = (k,v) -> {
            if(finalFrameMap.get(k) == null || !finalFrameMap.get(k).equals(v)){
                changedOrMissingBlocks.put((String)k,(String)v);
            }
        };

        selectionMap.forEach(getChangedOrMissing);

        return changedOrMissingBlocks;
    }
}
