package us.kg.gjyyds1;

import org.bukkit.Location;
import org.bukkit.World;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {

    private Connection connection;
    private GJGuilds plugin;

    public DatabaseManager(GJGuilds plugin) {
        this.plugin = plugin;
    }

    public void initDatabase() {
        try {
            String url = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/gjguilds.db";
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

    public void closeDatabaseConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Guild> loadAllGuilds() {
        Map<String, Guild> guilds = new HashMap<>();
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
                    World world = plugin.getServer().getWorld(worldName);
                    if (world != null) {
                        Location location = new Location(world, x, y, z);
                        guild.addWarp(new Warp(warpName, cost, location));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guilds;
    }

    public void saveGuildToDatabase(String guildName, UUID leader) {
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

    public void saveMemberToDatabase(String guildName, UUID memberId) {
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

    public void updateGuildInDatabase(String guildName, Guild guild) {
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

    public void saveWarpToDatabase(String guildName, String warpName, double cost, Location location) {
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

    public void removeWarpFromDatabase(String guildName, String warpName) {
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

    public void saveTpPointToDatabase(String guildName, Location location) {
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

    public void removeGuildFromDatabase(String guildName) {
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
}



