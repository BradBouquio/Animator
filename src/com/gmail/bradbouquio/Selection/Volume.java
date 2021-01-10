package com.gmail.bradbouquio.Selection;


import org.bukkit.Location;

public class Volume {

    private int minX,maxX,minY,maxY,minZ,maxZ;
    private int length,width,height,volume;

    Volume(Location one, Location two){
        calculateVolume(one,two);
    }

    private void calculateVolume(Location one, Location two) {
        minX = Math.min(one.getBlockX(), two.getBlockX());
        maxX = Math.max(one.getBlockX(), two.getBlockX());
        minY = Math.min(one.getBlockY(), two.getBlockY());
        maxY = Math.max(one.getBlockY(), two.getBlockY());
        minZ = Math.min(one.getBlockZ(), two.getBlockZ());
        maxZ = Math.max(one.getBlockZ(), two.getBlockZ());

        length = maxX-minX;
        width = maxZ-minZ;
        height = maxY-minY;

        volume = length*width*height;
    }


    public int getMinX() { return minX; }
    public int getMaxX() { return  maxX; }
    public int getMinY() { return minY; }
    public int getMaxY() { return maxY; }
    public int getMinZ() { return minZ; }
    public int getMaxZ() { return maxZ; }

    public int getLength() { return length; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getVolume() { return volume; }
}
