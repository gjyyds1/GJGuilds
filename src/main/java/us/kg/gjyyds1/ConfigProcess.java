package us.kg.gjyyds1;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Location;
import org.bukkit.World;

public class ConfigProcess {

    private FileConfiguration config;

    public ConfigProcess(FileConfiguration config) {
        this.config = config;
    }

    public Location getArenaLocation() {
        World world = Bukkit.getWorld(config.getString("arena.world"));
        double x = config.getDouble("arena.x");
        double y = config.getDouble("arena.y");
        double z = config.getDouble("arena.z");
        float yaw = (float) config.getDouble("arena.yaw", 0.0);
        float pitch = (float) config.getDouble("arena.pitch", 0.0);
        return new Location(world, x, y, z, yaw, pitch);
    }
}



