package com.gmail.bradbouquio.Selection;

import com.gmail.bradbouquio.Exception.WorldMismatchException;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class Selection {
    private Location one;
    private Location two;
    private Map<String, String> selectionBlocks;
    private World worldOne;
    private World worldTwo;
    private Volume vol;

    public void setOne(Location one){
        this.one = one;
        worldOne = one.getWorld();
        if(two != null && worldsMatch()) vol = new Volume(one,two);
    }

    public void setTwo(Location two){
        this.two = two;
        worldTwo = two.getWorld();
        if(one != null && worldsMatch()) vol = new Volume(one,two);
    }

    public Location getOne(){ return one; }
    public Location getTwo(){ return two; }

    public int getVolume(){
        if(vol != null) return vol.getVolume();
        else return 0;
    }

    public boolean worldsMatch(){
        String name1 = worldOne.getName();
        String name2 = worldTwo.getName();
        if(one != null && two != null && name1.equals(name2)) return true;
        else return false;
    }

    public World getWorld() throws WorldMismatchException {
        if(worldsMatch()) return one.getWorld();
        else throw new WorldMismatchException("Worlds do not match for Selection Point 1 and Selection Point 2");
    }

    public Map<String, String> getSelectionBlocks() throws WorldMismatchException {
        World world = getWorld();
        selectionBlocks = new HashMap<>(vol.getVolume());

        for(int x = vol.getMinX(); x <= vol.getMaxX(); x++){
            for(int y = vol.getMinY(); y <= vol.getMaxY(); y++){
                for(int z = vol.getMinZ(); z <= vol.getMaxZ(); z++){
                    selectionBlocks.put(x + "," + y + "," + z, world.getBlockAt(x,y,z).getBlockData().getMaterial().name());
                }
            }
        }
        return selectionBlocks;
    }

    public boolean pointsMatch() {
        if(one.equals(two)) return true;
        else return false;
    }
}
