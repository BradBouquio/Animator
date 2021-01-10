package com.gmail.bradbouquio.Util;

import org.bukkit.Location;
import org.bukkit.World;

public class Convert {

    public static Location stringCoordsToLocation(String coords, World world){
        Location loc;
        String[] coordsArr = coords.split(",");
        loc = new Location(world, Double.valueOf(coordsArr[0]), Double.valueOf(coordsArr[1]), Double.valueOf(coordsArr[2]));
        return loc;
    }
}
