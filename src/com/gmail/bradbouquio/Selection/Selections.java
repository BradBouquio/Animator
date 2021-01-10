package com.gmail.bradbouquio.Selection;

import java.util.HashMap;
import java.util.Map;

public class Selections {

    private static Map<String, Selection> playerSelectionMap = new HashMap<>();

    public static Selection get(String playerName){
        return playerSelectionMap.get(playerName);
    }

    public static void put(String player, Selection sel){
        playerSelectionMap.put(player, sel);
    }
}
