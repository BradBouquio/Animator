package com.gmail.bradbouquio.Listener;

import com.gmail.bradbouquio.Selection.Selection;
import com.gmail.bradbouquio.Selection.Selections;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void blockBroken(PlayerInteractEvent hitEvent) {
        Player player = hitEvent.getPlayer();
        if(player.getInventory().getItemInMainHand().getItemMeta() == null || !player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("Animation Wand")) return;
        hitEvent.setCancelled(true);
        Block hitBlock = hitEvent.getClickedBlock();
        if(hitBlock == null) return;

        if(hitEvent.getAction() == Action.LEFT_CLICK_BLOCK){
            Selection sel = Selections.get(player.getDisplayName());
            if(sel==null) sel = new Selection();
            sel.setOne(hitBlock.getLocation());
            Selections.put(player.getDisplayName(), sel);
            player.sendMessage("First position set to " + formatLocationCoords(hitBlock.getLocation()));
        }

        if(hitEvent.getAction() == Action.RIGHT_CLICK_BLOCK){
            Selection sel = Selections.get(player.getDisplayName());
            if(sel==null) sel = new Selection();
            sel.setTwo(hitBlock.getLocation());
            Selections.put(player.getDisplayName(), sel);
            player.sendMessage("Second position set to " + formatLocationCoords(hitBlock.getLocation()));
        }

    }

    private String formatLocationCoords(Location loc){
        return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
    }
}
