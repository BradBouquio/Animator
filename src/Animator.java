import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Animator extends JavaPlugin {
    public static Map<String, List<Location>> playerMap;
    public static Map<String, Selection> playerSelection;
    public static Animator plugin;
    public static Map<String, BukkitRunnable> runningTasks;


    @Override
    public void onEnable() {
        try {
            Files.createDirectories(Paths.get("plugins/Animator/animations"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin = this;
        playerMap = new HashMap<>();
        playerSelection = new HashMap<>();
        runningTasks = new HashMap<>();
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        //this.getCommand("anim").setTabCompleter(new CommandCompleter());
        this.getCommand("ani").setExecutor(new MainCommand());
    }

}
