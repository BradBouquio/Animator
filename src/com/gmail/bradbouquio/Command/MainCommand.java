package com.gmail.bradbouquio.Command;

import com.gmail.bradbouquio.Animation.Animation;
import com.gmail.bradbouquio.Animation.Animations;
import com.gmail.bradbouquio.Animator;
import com.gmail.bradbouquio.Exception.WorldMismatchException;
import com.gmail.bradbouquio.Selection.Selection;
import com.gmail.bradbouquio.Selection.Selections;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainCommand implements CommandExecutor {

    private File saveFile;
    private YamlConfiguration yml;
    private String animationName;
    private int lastFrame;
    private Selection selection;
    private int playedFrame = 0;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] subcommand) {
        Player player = commandSender instanceof Player ? (Player) commandSender : null;
        String animationName;

        if(subcommand[0].equals("frame")){
            if(subcommand[1] == null) commandSender.sendMessage("Command Usage: /ani frame (next|save) [animationName]");
            if("next".equals(subcommand[1])){
                if(player == null) {
                    commandSender.sendMessage("Only a player can use that command!");
                    return true;
                }
                if(subcommand[2] == null){
                    player.sendMessage("Command usage: /ani frame next [animationName]");
                    return true;
                } else animationName = subcommand[2];

                Selection playerSelection = Selections.get(player.getDisplayName());
                if(playerSelection == null) player.sendMessage("Use your wand to create a selection first! /ani wand");
                Animation animation = Animations.getAnimation(player.getDisplayName(), animationName);

                final ExecutorService service = Executors.newFixedThreadPool(4);
                final Runnable createFrameRunnable = () -> {
                    try {
                        animation.createNextFrame(playerSelection);
                    } catch (WorldMismatchException e) {
                        player.sendMessage(e.getMessage());
                    } catch (Throwable t){
                        t.printStackTrace();
                    }
                };

                final Future<?> createFrameRunnableHandle = service.submit(createFrameRunnable);

//                try {
//                    animation.createNextFrame(playerSelection);
//                } catch (WorldMismatchException e) {
//                    player.sendMessage(e.getMessage());
//                }

                //saveSelectionChanges(player);
            }

// Not sure what I was doing with this subcommand
//            if(subcommand[1] != null && subcommand[1].equals("save")){
//                if(subcommand[2] != null) {
//                    saveFrameToFile(commandSender);
//                    return true;
//                } else return false;
//            }

// Will fix this shortly
//            try {
//                Integer frameNumber = Integer.parseInt(subcommand[1]);
//                runFrame(subcommand[1]);
//                return true;
//            } catch (NumberFormatException nfe){}
//
//            return false;
        }

        if(subcommand[0].equals("edit")){
            if("origin".equals(subcommand[1])) {
                if(subcommand.length > 2){
                    Selection playerSelection = Selections.get(player.getDisplayName());
                    if(playerSelection == null) player.sendMessage("Use your wand to create a selection first! /ani wand");
                    Animation animation = Animations.getAnimation(((Player) commandSender).getDisplayName(), subcommand[2]);
                    animation.editOrigin(playerSelection);
                } else player.sendMessage("Command Usage: /ani edit origin [animationName]");
                return true;
            } else return false;
        }

        if(subcommand[0].equals("compress")){
            if(subcommand[1] != null) {
                Animation animation = Animations.getAnimation(((Player) commandSender).getDisplayName(), subcommand[1]);
                animation.compress();
                return true;
            } else return false;
        }

        if(subcommand[0].equals("play")){
            if(subcommand[1] != null) {
                int ticksBetweenFrames = subcommand.length > 2 ? Integer.valueOf(subcommand[2]) : 20;
                String loop = subcommand.length > 3 ? subcommand[3] : "";
                Animation animation = Animations.getAnimation(((Player) commandSender).getDisplayName(), subcommand[1]);
                animation.playAnimation(ticksBetweenFrames, loop.equals("loop"));
                return true;
            } else return false;
        }

        if(subcommand[0].equals("cancel")){
            if(subcommand.length < 2) return false;
            Animator.runningTasks.get(subcommand[1]).cancel();
        }

        if(subcommand[0].equals("wand")){
            getWand(commandSender);
            return true;
        }

        return false;
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
