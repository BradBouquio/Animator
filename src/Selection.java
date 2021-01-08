import org.bukkit.Location;
import org.bukkit.World;

public class Selection {
    private Location one;
    private Location two;

    public void setOne(Location one){
        this.one = one;
    }

    public void setTwo(Location two){
        this.two = two;
    }

    public Location getOne(){
        return one;
    }

    public Location getTwo(){
        return two;
    }

    public World getWorld() {
        String name1 = one.getWorld().getName();
        String name2 = two.getWorld().getName();
        if(one != null && two != null && name1.equals(name2)) return one.getWorld();
        else return null;
    }
}
