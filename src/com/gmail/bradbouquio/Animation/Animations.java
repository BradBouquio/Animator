package com.gmail.bradbouquio.Animation;

import java.util.HashMap;
import java.util.Map;

public class Animations {

    private static Map<String, Animation> playerAnimationMap = new HashMap<>();

    public static Animation getAnimation(String playerName, String animationName){
        return playerAnimationMap.getOrDefault(playerName, new Animation(playerName, animationName));
    }
}
