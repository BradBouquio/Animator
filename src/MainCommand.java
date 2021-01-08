import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainCommand implements CommandExecutor {

    private File saveFile;
    private YamlConfiguration yml;
    private String animationName;
    private int lastFrame;
    private Selection selection;
    private int playedFrame = 0;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = commandSender instanceof Player ? (Player) commandSender : null;

        if(args[0].equals("frame")){
            if(args[1] != null && args[1].equals("next")){
                if(args[2] != null) {
                    if(player != null) {
                        animationName = args[2];
                        saveSelectionChanges(player);
                    }
                    else commandSender.sendMessage("Only players can send that command!");
                    return true;
                } else return false;
            }

            if(args[1] != null && args[1].equals("save")){
                if(args[2] != null) {
                    saveFrameToFile(commandSender);
                    return true;
                } else return false;
            }
            try {
                Integer frameNumber = Integer.parseInt(args[1]);
                runFrame(args[1]);
                return true;
            } catch (NumberFormatException nfe){}

            return false;
        }

        if(args[0].equals("play")){
            if(args[1] != null) {
                int ticksBetweenFrames = args.length > 2 ? Integer.valueOf(args[2]) : 20;
                String loop = args.length > 3 ? args[3] : "";
                animationName = args[1];
                playAnimation(ticksBetweenFrames, loop.equals("loop"));
                return true;
            } else return false;
        }

        if(args[0].equals("cancel")){
            if(args.length < 2) return false;
            Animator.runningTasks.get(args[1]).cancel();
        }

        if(args[0].equals("wand")){
            getWand(commandSender);
            return true;
        }

        return false;
    }

    private void playAnimation(int ticksBetweenFrames, boolean loop) {
    int framesToPlay = getAnimationFile().getKeys(false).size();

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

//        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//
//        final Runnable playFrameRunnable = () -> runFrame(String.valueOf(playedFrame));
//
//        final ScheduledFuture<?> playFrameRunnableHandle = scheduler.scheduleAtFixedRate(playFrameRunnable, 1, 1, TimeUnit.SECONDS);
//        //scheduler.schedule(() -> playFrameRunnableHandle.cancel(true), 60 * 60, TimeUnit.SECONDS);


    }

    private void saveSelectionChanges(Player player) {
        yml = getAnimationFile();
        lastFrame = yml.getKeys(false).size()-1;
        List<Location> changedBlocks = getChangedSelectionBlocks(player);
        Animator.playerMap.put(player.getDisplayName(), changedBlocks);
        saveFrameToFile(player);
    }

    private List<Location> getChangedSelectionBlocks(Player player) {
        List<Location> changedBlocks = new ArrayList<>();
        selection = Animator.playerSelection.get(player.getDisplayName());
        World world = selection.getWorld();
        Location loc1 = selection.getOne();
        Location loc2 = selection.getTwo();

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        for(int x = minX; x <= maxX; x++){
            for(int y = minY; y <= maxY; y++){
                for(int z = minZ; z <= maxZ; z++){

                    //This is the bug
                    //Material lastMat = lastFramedBlockMaterial(lastFrame,x,y,z);

                    if(lastFrame<0 || lastFramedBlockMaterial(lastFrame,x,y,z) == null) {
                        changedBlocks.add(new Location(world,x,y,z));
                        continue;
                    }
                    if(!lastFramedBlockMaterial(lastFrame,x,y,z).name().equals(world.getBlockAt(x,y,z).getType().name())) {
                        changedBlocks.add(new Location(world,x,y,z));
                    }
                }
            }
        }

        return changedBlocks;
    }

    //Recursion! Find the last known material for this location
    private Material lastFramedBlockMaterial(int frame, int x, int y, int z) {
        Set<String> blocksChangedInLastFrame = yml.getConfigurationSection(String.valueOf(frame)).getKeys(false);
        for(String blockIndex : blocksChangedInLastFrame){
            String[] coords = yml.getString(frame + "." + blockIndex + ".coords").split(",");
            if(Integer.parseInt(coords[0]) == x && Integer.parseInt(coords[1]) == y && Integer.parseInt(coords[2]) == z){
                return Material.getMaterial(yml.getString(frame + "." + blockIndex + ".Material"));
            }
        }
        if(frame == 0) return null;
        else return lastFramedBlockMaterial(frame-1,x,y,z);
    }

    private YamlConfiguration getAnimationFile(){
        String pathName = "plugins/Animator/animations/" + animationName + ".yml";
        Path animFile = Paths.get(pathName);
        if(!Files.exists(animFile)){
            try {
                Files.createFile(animFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        saveFile = new File(pathName);
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(saveFile);
        return yml;
    }

    private void runFrame(String frame) {
        playedFrame = Integer.valueOf(frame) + 1;
        if (yml==null) yml = getAnimationFile();
        if(yml.getConfigurationSection(frame) == null) return;
        Set<String> changeKeys = yml.getConfigurationSection(frame).getKeys(false);
        for(String blockChange : changeKeys){
            String[] coords = yml.getString(frame + "." + blockChange + ".coords").split(",");
            World world = Bukkit.getWorld("arena");
            Location loc = new Location(world,Double.valueOf(coords[0]),Double.valueOf(coords[1]),Double.valueOf(coords[2]));
            world.getBlockAt(loc).setType(Material.getMaterial(yml.getString(frame + "." + blockChange + ".Material")));
        }
    }

    private void saveFrameToFile(CommandSender sender) {
        if(yml==null) yml = getAnimationFile();
        int section = yml.getKeys(false) == null ? 0 : yml.getKeys(false).size();
        yml.createSection(Integer.toString(section));

        List<Location> locationsToWrite = Animator.playerMap.get(((Player)sender).getDisplayName());
        for(int i = 0; i < locationsToWrite.size(); i++) {
            Location loc = locationsToWrite.get(i);
            yml.createSection(section + "." + i);
            yml.addDefault(section + "." + i + ".Material", loc.getBlock().getBlockData().getMaterial().name());
            yml.addDefault(section + "." + i + ".coords", loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
        }
        try {
            yml.options().copyDefaults(true);
            yml.save(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Animator.playerMap.remove(((Player)sender).getDisplayName());
    }


    private void getWand(CommandSender commandSender) {
        ItemStack wand = new ItemStack(Material.WOODEN_HOE);
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName("Animation Wand");
        wand.setItemMeta(meta);

        if(commandSender instanceof Player){
            ((Player) commandSender).getInventory().addItem(wand);
        }
    }

}
