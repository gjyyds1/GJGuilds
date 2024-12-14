package us.kg.gjyyds1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import me.yic.xconomy.api.XConomyAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GJGuilds extends JavaPlugin {

    private Map<String, Guild> guilds = new HashMap<>();
    private ConfigProcess configProcess;
    private MessageHandler messageHandler;
    private DatabaseManager databaseManager;
    private PvPState pvPState;
    private PermissionManager permissionManager;
    private XConomyAPI xcapi;
    private Map<UUID, Location> playerLocations = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("插件已启动");
        getLogger().info("作者：gjyyds1");

        // Load configuration
        saveDefaultConfig();
        configProcess = new ConfigProcess(getConfig());
        messageHandler = new MessageHandler(configProcess);

        // Register commands
        this.getCommand("guild").setExecutor(new GuildCommandExecutor(this));

        // Register permissions
        permissionManager = new PermissionManager(this);

        // Initialize SQLite database
        databaseManager = new DatabaseManager(this);
        databaseManager.initDatabase();
        loadGuildsFromDatabase();

        // Initialize XConomyAPI
        Plugin xconomyPlugin = Bukkit.getServer().getPluginManager().getPlugin("Xconomy");
        if (xconomyPlugin != null && xconomyPlugin.isEnabled()) {
            xcapi = (XConomyAPI) xconomyPlugin;
            getLogger().info("成功加载XConomyAPI");
        } else {
            getLogger().severe("Xconomy 插件未启用或未找到！");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize PvP state
        pvPState = new PvPState();
    }

    @Override
    public void onDisable() {
        getLogger().info("插件已关闭");
        databaseManager.closeDatabaseConnection();
    }

    private void loadGuildsFromDatabase() {
        guilds = databaseManager.loadAllGuilds();
    }

    public Map<String, Guild> getGuilds() {
        return guilds;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PvPState getPvPState() {
        return pvPState;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public XConomyAPI getXcapi() {
        return xcapi;
    }

    public String getPlayerGuild(UUID playerId) {
        for (Map.Entry<String, Guild> entry : guilds.entrySet()) {
            if (entry.getValue().isMember(playerId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void savePlayerLocation(Player player) {
        playerLocations.put(player.getUniqueId(), player.getLocation());
    }

    public Location restorePlayerLocation(Player player) {
        return playerLocations.remove(player.getUniqueId());
    }

    public ConfigProcess getConfigProcess() {
        return configProcess;
    }
}



