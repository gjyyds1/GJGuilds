package us.kg.gjyyds1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.math.BigDecimal;
import me.yic.xconomy.api.XConomyAPI;
import us.kg.gjyyds1.MoneyTransaction;

public final class GJGuilds extends JavaPlugin {

    private Map<String, Guild> guilds = new HashMap<>();
    private FileConfiguration config;
    private Connection connection;
    private XConomyAPI xcapi;

    @Override
    public void onEnable() {
        getLogger().info("插件已启动");
        getLogger().info("作者：gjyyds1");

        // Load configuration
        saveDefaultConfig();
        config = getConfig();

        // Register commands
        this.getCommand("guild").setExecutor(new GuildCommandExecutor());

        // Register permissions
        getServer().getPluginManager().addPermission(new Permission("gj.guild.create"));
        getServer().getPluginManager().addPermission(new Permission("gj.guild.join"));
        getServer().getPluginManager().addPermission(new Permission("gj.guild.save.exp"));
        getServer().getPluginManager().addPermission(new Permission("gj.guild.save.money"));
        getServer().getPluginManager().addPermission(new Permission("gj.guild.pvp"));
        getServer().getPluginManager().addPermission(new Permission("gj.guild.warp.create"));
        getServer().getPluginManager().addPermission(new Permission("gj.guild.warp.remove"));
        getServer().getPluginManager().addPermission(new Permission("gj.guild.tpset"));
        getServer().getPluginManager().addPermission(new Permission("gj.guild.tp"));
        getServer().getPluginManager().addPermission(new Permission("gj.guild.admin.remove"));

        // Initialize SQLite database
        initDatabase();
        loadGuildsFromDatabase();

        // Initialize XconomyAPI
        Plugin xconomyPlugin = Bukkit.getServer().getPluginManager().getPlugin("Xconomy");
        if (xconomyPlugin != null && xconomyPlugin.isEnabled()) {
            xcapi = (XConomyAPI) xconomyPlugin;
            getLogger().info("成功加载XconomyAPI");
        } else {
            getLogger().severe("Xconomy 插件未启用或未找到！");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("插件已关闭");
        closeDatabaseConnection();
    }

    private void initDatabase() {
        try {
            String url = "jdbc:sqlite:" + getDataFolder().getAbsolutePath() + "/gjguilds.db";
            connection = DriverManager.getConnection(url);
            String sql = "CREATE TABLE IF NOT EXISTS guilds (\n"
                    + "	guild_name text PRIMARY KEY,\n"
                    + "	leader_uuid text NOT NULL,\n"
                    + "	exp_pool integer DEFAULT 0,\n"
                    + "	bank real DEFAULT 0.0\n"
                    + ");\n"
                    + "CREATE TABLE IF NOT EXISTS members (\n"
                    + "	guild_name text NOT NULL,\n"
                    + "	player_uuid text NOT NULL,\n"
                    + "	is_leader boolean DEFAULT false,\n"
                    + "	FOREIGN KEY (guild_name) REFERENCES guilds (guild_name)\n"
                    + ");\n"
                    + "CREATE TABLE IF NOT EXISTS warps (\n"
                    + "	guild_name text NOT NULL,\n"
                    + "	warp_name text NOT NULL,\n"
                    + "	cost real NOT NULL,\n"
                    + "	x real NOT NULL,\n"
                    + "	y real NOT NULL,\n"
                    + "	z real NOT NULL,\n"
                    + "	world_name text NOT NULL,\n"
                    + "	FOREIGN KEY (guild_name) REFERENCES guilds (guild_name)\n"
                    + ");";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadGuildsFromDatabase() {
        try {
            String sql = "SELECT * FROM guilds;";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String guildName = rs.getString("guild_name");
                UUID leaderUuid = UUID.fromString(rs.getString("leader_uuid"));
                int expPool = rs.getInt("exp_pool");
                double bank = rs.getDouble("bank");
                Guild guild = new Guild(leaderUuid);
                guild.addToExpPool(expPool);
                guild.addToBank(bank);
                guilds.put(guildName, guild);

                // Load members
                String memberSql = "SELECT player_uuid, is_leader FROM members WHERE guild_name = ?;";
                PreparedStatement memberStmt = connection.prepareStatement(memberSql);
                memberStmt.setString(1, guildName);
                ResultSet memberRs = memberStmt.executeQuery();
                while (memberRs.next()) {
                    UUID memberUuid = UUID.fromString(memberRs.getString("player_uuid"));
                    boolean isLeader = memberRs.getBoolean("is_leader");
                    if (isLeader) {
                        guild.setLeader(memberUuid);
                    } else {
                        guild.addMember(memberUuid);
                    }
                }

                // Load warps
                String warpSql = "SELECT warp_name, cost, x, y, z, world_name FROM warps WHERE guild_name = ?;";
                PreparedStatement warpStmt = connection.prepareStatement(warpSql);
                warpStmt.setString(1, guildName);
                ResultSet warpRs = warpStmt.executeQuery();
                while (warpRs.next()) {
                    String warpName = warpRs.getString("warp_name");
                    double cost = warpRs.getDouble("cost");
                    double x = warpRs.getDouble("x");
                    double y = warpRs.getDouble("y");
                    double z = warpRs.getDouble("z");
                    String worldName = warpRs.getString("world_name");
                    Location location = new Location(Bukkit.getWorld(worldName), x, y, z);
                    guild.addWarp(new Warp(warpName, cost, location));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeDatabaseConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class GuildCommandExecutor implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "只有玩家才能执行此命令！");
                return true;
            }
            Player player = (Player) sender;
            UUID playerId = player.getUniqueId();

            if (args.length == 0) {
                player.sendMessage(ChatColor.YELLOW + "/guild create <公会名称>");
                player.sendMessage(ChatColor.YELLOW + "/guild join <公会名称>");
                player.sendMessage(ChatColor.YELLOW + "/guild save exp/money <数量>/all");
                player.sendMessage(ChatColor.YELLOW + "/guild pvp join/exit");
                player.sendMessage(ChatColor.YELLOW + "/guild warp create <区域名称> <进入时需要扣除的金额>");
                player.sendMessage(ChatColor.YELLOW + "/guild warp remove <区域名称>");
                player.sendMessage(ChatColor.YELLOW + "/guild tp <区域名称>");
                player.sendMessage(ChatColor.YELLOW + "/guild tpset");
                player.sendMessage(ChatColor.YELLOW + "/guild remove <公会名称>");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "create":
                    if (!player.hasPermission("gj.guild.create") && !player.isOp()) {
                        player.sendMessage(ChatColor.RED + "你没有权限创建公会！");
                        return true;
                    }
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "请输入公会名称！");
                        return true;
                    }
                    String guildName = args[1];
                    if (guilds.containsKey(guildName)) {
                        player.sendMessage(ChatColor.RED + "该公会已经存在！");
                        return true;
                    }
                    guilds.put(guildName, new Guild(playerId));
                    saveGuildToDatabase(guildName, playerId);
                    player.sendMessage(ChatColor.GREEN + "成功创建公会: " + guildName);
                    break;
                case "join":
                    if (!player.hasPermission("gj.guild.join")) {
                        player.sendMessage(ChatColor.RED + "你没有权限加入公会！");
                        return true;
                    }
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "请输入公会名称！");
                        return true;
                    }
                    guildName = args[1];
                    if (!guilds.containsKey(guildName)) {
                        player.sendMessage(ChatColor.RED + "该公会不存在！");
                        return true;
                    }
                    Guild guild = guilds.get(guildName);
                    guild.addMember(playerId);
                    saveMemberToDatabase(guildName, playerId);
                    player.sendMessage(ChatColor.GREEN + "成功加入公会: " + guildName);
                    break;
                case "save":
                    if (args.length < 3) {
                        player.sendMessage(ChatColor.RED + "使用方法: /guild save exp/money <数量>/all");
                        return true;
                    }

                    String type = args[1];
                    int amount;

                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "请输入有效的数字！");
                        return true;
                    }

                    if (!player.hasPermission("gj.guild.save." + type)) {
                        player.sendMessage(ChatColor.RED + "你没有权限保存" + type + "！");
                        return true;
                    }

                    guildName = getPlayerGuild(playerId);
                    if (guildName == null) {
                        player.sendMessage(ChatColor.RED + "你不在任何公会中！");
                        return true;
                    }

                    guild = guilds.get(guildName);

                    if ("exp".equalsIgnoreCase(type)) {
                        // 扣除经验值
                        if (deductPlayerExperience(player, amount)) {
                            guild.addToExpPool(amount);
                            updateGuildInDatabase(guildName, guild);
                            player.sendMessage(ChatColor.GREEN + "成功保存经验值：" + amount);
                        } else {
                            player.sendMessage(ChatColor.RED + "保存经验值失败！");
                        }
                    } else if ("money".equalsIgnoreCase(type)) {
                        BigDecimal bdAmount = BigDecimal.valueOf(amount);

                        // 定义非玩家账户名称（公会金库）
                        String nonPlayerAccount = "guild_" + guildName;

                        // 检查并创建非玩家账户，然后进行“1操作”
                        MoneyTransaction transaction = new MoneyTransaction(xcapi);
                        boolean success = transaction.performOperation(player.getUniqueId(), player.getName(), nonPlayerAccount, bdAmount);

                        if (success) {
                            // 公会金库增加金额
                            guild.addToBank(amount);
                            updateGuildInDatabase(guildName, guild);
                            player.sendMessage(ChatColor.GREEN + "成功保存：" + amount);
                        } else {
                            player.sendMessage(ChatColor.RED + "保存失败！请检查余额是否足够。");
                        }
                    } else if ("all".equalsIgnoreCase(type)) {
                        // 保存所有逻辑
                        player.sendMessage(ChatColor.GREEN + "未实现");
                    } else {
                        player.sendMessage(ChatColor.RED + "未知类型：" + type);
                    }
                    break;

                case "pvp":
                    if (!player.hasPermission("gj.guild.pvp") && !player.isOp()) {
                        player.sendMessage(ChatColor.RED + "你没有权限参与PVP！");
                        return true;
                    }
                    if (args.length < 2 || (!args[1].equalsIgnoreCase("join") && !args[1].equalsIgnoreCase("exit"))) {
                        player.sendMessage(ChatColor.RED + "使用方法: /guild pvp join/exit");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("join")) {
                        // Add player to PvP state
                        getPvPState().put(playerId, true);
                        player.sendMessage(ChatColor.GREEN + "成功加入PVP擂台");
                    } else {
                        getPvPState().remove(playerId);
                        player.sendMessage(ChatColor.GREEN + "成功离开PVP擂台");
                    }
                    break;
                case "warp":
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "请输入子命令！");
                        return true;
                    }
                    switch (args[1].toLowerCase()) {
                        case "create":
                            if (!player.hasPermission("gj.guild.warp.create")) {
                                player.sendMessage(ChatColor.RED + "你没有权限创建公会区域！");
                                return true;
                            }
                            if (args.length < 4) {
                                player.sendMessage(ChatColor.RED + "使用方法: /guild warp create <区域名称> <进入时需要扣除的金额>");
                                return true;
                            }
                            guildName = getPlayerGuild(playerId);
                            if (guildName == null) {
                                player.sendMessage(ChatColor.RED + "你不在任何公会中！");
                                return true;
                            }
                            guild = guilds.get(guildName);
                            if (!guild.isLeader(playerId)) {
                                player.sendMessage(ChatColor.RED + "你不是公会会长！");
                                return true;
                            }
                            String regionName = args[2];
                            double cost;
                            try {
                                cost = Double.parseDouble(args[3]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + "请输入有效的金额！");
                                return true;
                            }
                            Location location = player.getLocation();
                            guild.addWarp(new Warp(regionName, cost, location));
                            saveWarpToDatabase(guildName, regionName, cost, location);
                            player.sendMessage(ChatColor.GREEN + "成功创建公会区域: " + regionName);
                            break;
                        case "remove":
                            if (!player.hasPermission("gj.guild.warp.remove") && !player.isOp()) {
                                player.sendMessage(ChatColor.RED + "你没有权限删除公会区域！");
                                return true;
                            }
                            if (args.length < 3) {
                                player.sendMessage(ChatColor.RED + "使用方法: /guild warp remove <区域名称>");
                                return true;
                            }
                            guildName = getPlayerGuild(playerId);
                            if (guildName == null) {
                                player.sendMessage(ChatColor.RED + "你不在任何公会中！");
                                return true;
                            }
                            guild = guilds.get(guildName);
                            if (!guild.isLeader(playerId) && !player.hasPermission("gj.guild.admin.remove")) {
                                player.sendMessage(ChatColor.RED + "你不是公会会长且没有管理权限！");
                                return true;
                            }
                            regionName = args[2];
                            guild.removeWarp(regionName);
                            removeWarpFromDatabase(guildName, regionName);
                            player.sendMessage(ChatColor.GREEN + "成功删除公会区域: " + regionName);
                            break;
                        default:
                            player.sendMessage(ChatColor.RED + "未知子命令: " + args[1]);
                            return false;
                    }
                    break;
                case "tp":
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "请输入区域名称！");
                        return true;
                    }
                    guildName = getPlayerGuild(playerId);
                    if (guildName == null) {
                        player.sendMessage(ChatColor.RED + "你不在任何公会中！");
                        return true;
                    }
                    guild = guilds.get(guildName);
                    String regionName = args[1];
                    Warp warp = guild.getWarp(regionName);
                    if (warp != null) {
                        double cost = warp.getCost();
                        if (guild.getBank() >= cost) {
                            BigDecimal bdCost = BigDecimal.valueOf(cost);
                            if (xcapi.changeNonPlayerBalance(guildName, bdCost, false) == 0) {
                                guild.subtractFromBank(cost);
                                updateGuildInDatabase(guildName, guild);
                                player.teleport(warp.getLocation());
                                player.sendMessage(ChatColor.GREEN + "成功传送到区域: " + regionName);
                            } else {
                                player.sendMessage(ChatColor.RED + "扣除资金失败！");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "资金不足，无法传送！");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "该区域不存在！");
                    }
                    break;
                case "tpset":
                    if (!player.hasPermission("gj.guild.tpset")) {
                        player.sendMessage(ChatColor.RED + "你没有权限设置传送点！");
                        return true;
                    }
                    guildName = getPlayerGuild(playerId);
                    if (guildName == null) {
                        player.sendMessage(ChatColor.RED + "你不在任何公会中！");
                        return true;
                    }
                    guild = guilds.get(guildName);
                    guild.setTpPoint(player.getLocation());
                    saveTpPointToDatabase(guildName, player.getLocation());
                    player.sendMessage(ChatColor.GREEN + "成功设置传送点");
                    break;
                case "remove":
                    if (!player.hasPermission("gj.guild.admin.remove") && !player.isOp()) {
                        player.sendMessage(ChatColor.RED + "你没有权限删除公会！");
                        return true;
                    }
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "请输入公会名称！");
                        return true;
                    }
                    guildName = args[1];
                    if (!guilds.containsKey(guildName)) {
                        player.sendMessage(ChatColor.RED + "该公会不存在！");
                        return true;
                    }
                    guild = guilds.get(guildName);
                    if (!guild.isLeader(playerId) && !player.hasPermission("gj.guild.admin.remove")) {
                        player.sendMessage(ChatColor.RED + "你不是公会会长且没有管理权限！");
                        return true;
                    }
                    guilds.remove(guildName);
                    removeGuildFromDatabase(guildName);
                    player.sendMessage(ChatColor.GREEN + "成功删除公会: " + guildName);
                    break;
                default:
                    player.sendMessage(ChatColor.RED + "未知子命令: " + args[0]);
                    return false;
            }
            return true;
        }

        private String getPlayerGuild(UUID playerId) {
            for (Map.Entry<String, Guild> entry : guilds.entrySet()) {
                if (entry.getValue().isMember(playerId)) {
                    return entry.getKey();
                }
            }
            return null;
        }

        private boolean deductPlayerExperience(Player player, int amount) {
            // 扣除玩家经验
            if (player.getExp() < amount) {
                player.sendMessage(ChatColor.RED + "你的等级不足！");
                return false;
            } else {
                player.setExp(player.getExp() - amount);
                player.sendMessage(ChatColor.GRAY + "从你的经验中扣除了 " + amount + " 点经验。");
                return true; // Return true if deduction was successful
            }
        }
    }

    private static class Guild {
        private UUID leader;
        private Map<UUID, Boolean> members = new HashMap<>(); // True means member, False means invited
        private int expPool = 0;
        private double bank = 0.0;
        private Map<String, Warp> warps = new HashMap<>();
        private Location tpPoint;

        public Guild(UUID leader) {
            this.leader = leader;
            members.put(leader, true);
        }

        public boolean isLeader(UUID playerId) {
            return leader.equals(playerId);
        }

        public boolean isMember(UUID playerId) {
            return members.containsKey(playerId);
        }

        public void addMember(UUID playerId) {
            members.put(playerId, true);
        }

        public void removeMember(UUID playerId) {
            members.remove(playerId);
        }

        public int getExpPool() {
            return expPool;
        }

        public void addToExpPool(int amount) {
            expPool += amount;
        }

        public double getBank() {
            return bank;
        }

        public void addToBank(double amount) {
            bank += amount;
        }

        public void subtractFromBank(double amount) {
            bank -= amount;
        }

        public void setLeader(UUID leader) {
            this.leader = leader;
        }

        public void addWarp(Warp warp) {
            warps.put(warp.getName(), warp);
        }

        public void removeWarp(String name) {
            warps.remove(name);
        }

        public Warp getWarp(String name) {
            return warps.get(name);
        }

        public void setTpPoint(Location location) {
            this.tpPoint = location;
        }

        public Location getTpPoint() {
            return tpPoint;
        }
    }

    private static class Warp {
        private String name;
        private double cost;
        private Location location;

        public Warp(String name, double cost, Location location) {
            this.name = name;
            this.cost = cost;
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public double getCost() {
            return cost;
        }

        public Location getLocation() {
            return location;
        }
    }

    private void saveGuildToDatabase(String guildName, UUID leader) {
        try {
            String sql = "INSERT INTO guilds (guild_name, leader_uuid) VALUES (?, ?);";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, guildName);
            stmt.setString(2, leader.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveMemberToDatabase(String guildName, UUID memberId) {
        try {
            String sql = "INSERT INTO members (guild_name, player_uuid, is_leader) VALUES (?, ?, ?);";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, guildName);
            stmt.setString(2, memberId.toString());
            stmt.setBoolean(3, false);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateGuildInDatabase(String guildName, Guild guild) {
        try {
            String sql = "UPDATE guilds SET exp_pool = ?, bank = ? WHERE guild_name = ?;";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, guild.getExpPool());
            stmt.setDouble(2, guild.getBank());
            stmt.setString(3, guildName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveWarpToDatabase(String guildName, String warpName, double cost, Location location) {
        try {
            String sql = "INSERT INTO warps (guild_name, warp_name, cost, x, y, z, world_name) VALUES (?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, guildName);
            stmt.setString(2, warpName);
            stmt.setDouble(3, cost);
            stmt.setDouble(4, location.getX());
            stmt.setDouble(5, location.getY());
            stmt.setDouble(6, location.getZ());
            stmt.setString(7, location.getWorld().getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeWarpFromDatabase(String guildName, String warpName) {
        try {
            String sql = "DELETE FROM warps WHERE guild_name = ? AND warp_name = ?;";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, guildName);
            stmt.setString(2, warpName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveTpPointToDatabase(String guildName, Location location) {
        try {
            String sql = "UPDATE guilds SET tp_x = ?, tp_y = ?, tp_z = ?, tp_world = ? WHERE guild_name = ?;";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDouble(1, location.getX());
            stmt.setDouble(2, location.getY());
            stmt.setDouble(3, location.getZ());
            stmt.setString(4, location.getWorld().getName());
            stmt.setString(5, guildName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeGuildFromDatabase(String guildName) {
        try {
            String sql = "DELETE FROM guilds WHERE guild_name = ?;";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, guildName);
            stmt.executeUpdate();

            sql = "DELETE FROM members WHERE guild_name = ?;";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, guildName);
            stmt.executeUpdate();

            sql = "DELETE FROM warps WHERE guild_name = ?;";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, guildName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Map<UUID, Boolean> getPvPState() {
        return pvPState;
    }

    private Map<UUID, Boolean> pvPState = new HashMap<>();

    public XConomyAPI getXcapi() {
        return xcapi;
    }
}
