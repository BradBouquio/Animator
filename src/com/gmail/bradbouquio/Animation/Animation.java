package com.gmail.bradbouquio.Animation;

import com.gmail.bradbouquio.Animator;
import com.gmail.bradbouquio.Exception.WorldMismatchException;
import com.gmail.bradbouquio.File.AnimationFile;
import com.gmail.bradbouquio.Selection.Selection;
import com.gmail.bradbouquio.Util.CompareBlockMaps;
import com.gmail.bradbouquio.Util.Compression;
import com.gmail.bradbouquio.Util.FinalFrameBlocksMapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;

public class Animation {

    private String animationName;
    private String playerName;
    private AnimationFile animationFile;
    private int indexOfFinalFrame;
    int playedFrame = 0;

    Animation(String playerName, String animationName){
        this.playerName = playerName;
        this.animationName = animationName;
        this.animationFile = AnimationFile.getAnimationFile(playerName,animationName);
    }

    public YamlConfiguration getYml(){
        return animationFile.getYml();
    }

    public void createNextFrame(Selection selection) throws WorldMismatchException {
        indexOfFinalFrame = animationFile.getYml().getKeys(false).size()-1;
        Map<String, String> selectionBlocks = selection.getSelectionBlocks();
        Map<String, String> finalFrameBlocks = FinalFrameBlocksMapper.lastFramedBlockMaterials(animationFile.getYml(), selection.getWorld(), indexOfFinalFrame);
        Map<String, String> changedOrMissingBlocks = CompareBlockMaps.compare(selectionBlocks,finalFrameBlocks);
        animationFile.saveFrameToFile(changedOrMissingBlocks);
    }

    public void playAnimation(int ticksBetweenFrames, boolean loop) {
        int framesToPlay = animationFile.getYml().getKeys(false).size();
        BukkitRunnable bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                runFrame(String.valueOf(playedFrame));;
                if(playedFrame >= framesToPlay) {
                    playedFrame = 0;
                    if(!loop) this.cancel();
                }
            }
        };
        bukkitTask.runTaskTimer(Animator.plugin, 0, ticksBetweenFrames);
        Animator.runningTasks.put(animationName,bukkitTask);
    }

    private void runFrame(String frame) {
        playedFrame = Integer.valueOf(frame) + 1;
        YamlConfiguration yml = animationFile.getYml();
        if(yml.getConfigurationSection(frame) == null) return;
        Set<String> changeKeys = yml.getConfigurationSection(frame).getKeys(false);
        for(String blockChange : changeKeys){
            String[] coords = yml.getString(frame + "." + blockChange + ".coords").split(",");
            World world = Bukkit.getWorld("arena");
            Location loc = new Location(world,Double.valueOf(coords[0]),Double.valueOf(coords[1]),Double.valueOf(coords[2]));
            world.getBlockAt(loc).setType(Material.getMaterial(yml.getString(frame + "." + blockChange + ".Material")));
        }
    }

    public void compress(){
        Compression.compress(animationFile);
    }

}
