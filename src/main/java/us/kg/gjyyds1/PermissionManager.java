package us.kg.gjyyds1;

import org.bukkit.entity.Player;

public class PermissionManager {

    private GJGuilds plugin;

    public PermissionManager(GJGuilds plugin) {
        this.plugin = plugin;
    }

    public boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission) || player.isOp();
    }
}



