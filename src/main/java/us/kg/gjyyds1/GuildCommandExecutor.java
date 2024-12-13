package us.kg.gjyyds1;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import java.math.BigDecimal;
import java.util.UUID;

public class GuildCommandExecutor implements CommandExecutor {

    private GJGuilds plugin;

    public GuildCommandExecutor(GJGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageHandler().sendMessage((Player) sender, "not-player");
            return true;
        }
        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                handleCreate(player, args);
                break;
            case "join":
                handleJoin(player, args);
                break;
            case "save":
                handleSave(player, args);
                break;
            case "pvp":
                handlePVP(player, args);
                break;
            case "warp":
                handleWarp(player, args);
                break;
            case "tp":
                handleTP(player, args);
                break;
            case "tpset":
                handleTPSet(player, args);
                break;
            case "remove":
                handleRemove(player, args);
                break;
            default:
                plugin.getMessageHandler().sendMessage(player, "unknown-command", args[0]);
                return false;
        }
        return true;
    }

    private void showHelp(Player player) {
        plugin.getMessageHandler().sendMessage(player, "command-create");
        plugin.getMessageHandler().sendMessage(player, "command-join");
        plugin.getMessageHandler().sendMessage(player, "command-save");
        plugin.getMessageHandler().sendMessage(player, "command-pvp");
        plugin.getMessageHandler().sendMessage(player, "command-warp");
        plugin.getMessageHandler().sendMessage(player, "command-tp");
        plugin.getMessageHandler().sendMessage(player, "command-tpset");
        plugin.getMessageHandler().sendMessage(player, "command-remove");
    }

    private void handleCreate(Player player, String[] args) {
        if (!plugin.getPermissionManager().hasPermission(player, "gj.guild.create") && !player.isOp()) {
            plugin.getMessageHandler().sendMessage(player, "no-permission");
            return;
        }
        if (args.length < 2) {
            plugin.getMessageHandler().sendMessage(player, "missing-guild-name");
            return;
        }
        String guildName = args[1];
        if (plugin.getGuilds().containsKey(guildName)) {
            plugin.getMessageHandler().sendMessage(player, "guild-exists");
            return;
        }
        Guild guild = new Guild(player.getUniqueId());
        plugin.getGuilds().put(guildName, guild);
        plugin.getDatabaseManager().saveGuildToDatabase(guildName, player.getUniqueId());
        plugin.getMessageHandler().sendMessage(player, "guild-created", guildName);
    }

    private void handleJoin(Player player, String[] args) {
        if (!plugin.getPermissionManager().hasPermission(player, "gj.guild.join")) {
            plugin.getMessageHandler().sendMessage(player, "no-permission");
            return;
        }
        if (args.length < 2) {
            plugin.getMessageHandler().sendMessage(player, "missing-guild-name");
            return;
        }
        String guildName = args[1];
        if (!plugin.getGuilds().containsKey(guildName)) {
            plugin.getMessageHandler().sendMessage(player, "guild-not-found");
            return;
        }
        Guild guild = plugin.getGuilds().get(guildName);
        guild.addMember(player.getUniqueId());
        plugin.getDatabaseManager().saveMemberToDatabase(guildName, player.getUniqueId());
        plugin.getMessageHandler().sendMessage(player, "joined-guild", guildName);
    }

    private void handleSave(Player player, String[] args) {
        if (args.length < 3) {
            plugin.getMessageHandler().sendMessage(player, "invalid-save-command");
            return;
        }
        String type = args[1];
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            plugin.getMessageHandler().sendMessage(player, "invalid-number");
            return;
        }
        if (!plugin.getPermissionManager().hasPermission(player, "gj.guild.save." + type)) {
            plugin.getMessageHandler().sendMessage(player, "no-permission");
            return;
        }
        String guildName = plugin.getPlayerGuild(player.getUniqueId());
        if (guildName == null) {
            plugin.getMessageHandler().sendMessage(player, "not-in-guild");
            return;
        }
        Guild guild = plugin.getGuilds().get(guildName);
        if ("exp".equalsIgnoreCase(type)) {
            if (deductPlayerExperience(player, amount)) {
                guild.addToExpPool(amount);
                plugin.getDatabaseManager().updateGuildInDatabase(guildName, guild);
                plugin.getMessageHandler().sendMessage(player, "exp-saved", String.valueOf(amount));
            } else {
                plugin.getMessageHandler().sendMessage(player, "deduct-exp-failed");
            }
        } else if ("money".equalsIgnoreCase(type)) {
            BigDecimal bdAmount = BigDecimal.valueOf(amount);
            if (plugin.getXcapi().changePlayerBalance(player.getUniqueId(), player.getName(), bdAmount, false) == 0) {
                guild.subtractFromBank(amount);
                plugin.getDatabaseManager().updateGuildInDatabase(guildName, guild);
                plugin.getMessageHandler().sendMessage(player, "money-deducted", String.valueOf(amount));
            } else {
                plugin.getMessageHandler().sendMessage(player, "deduct-money-failed");
            }
        } else if ("all".equalsIgnoreCase(type)) {
            plugin.getMessageHandler().sendMessage(player, "resources-saved");
        } else {
            plugin.getMessageHandler().sendMessage(player, "unknown-type", type);
        }
    }

    private void handlePVP(Player player, String[] args) {
        if (!plugin.getPermissionManager().hasPermission(player, "gj.guild.pvp") && !player.isOp()) {
            plugin.getMessageHandler().sendMessage(player, "no-permission");
            return;
        }
        if (args.length < 2 || (!args[1].equalsIgnoreCase("join") && !args[1].equalsIgnoreCase("exit"))) {
            plugin.getMessageHandler().sendMessage(player, "invalid-pvp-command");
            return;
        }
        if (args[1].equalsIgnoreCase("join")) {
            plugin.getPvPState().put(player.getUniqueId(), true);
            plugin.getMessageHandler().sendMessage(player, "joined-pvp");
        } else {
            plugin.getPvPState().remove(player.getUniqueId());
            plugin.getMessageHandler().sendMessage(player, "left-pvp");
        }
    }

    private void handleWarp(Player player, String[] args) {
        if (args.length < 2) {
            plugin.getMessageHandler().sendMessage(player, "missing-subcommand");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "create":
                handleWarpCreate(player, args);
                break;
            case "remove":
                handleWarpRemove(player, args);
                break;
            default:
                plugin.getMessageHandler().sendMessage(player, "unknown-subcommand", args[1]);
                return;
        }
    }

    private void handleWarpCreate(Player player, String[] args) {
        if (!plugin.getPermissionManager().hasPermission(player, "gj.guild.warp.create")) {
            plugin.getMessageHandler().sendMessage(player, "no-permission");
            return;
        }
        if (args.length < 4) {
            plugin.getMessageHandler().sendMessage(player, "invalid-warp-create-command");
            return;
        }
        String guildName = plugin.getPlayerGuild(player.getUniqueId());
        if (guildName == null) {
            plugin.getMessageHandler().sendMessage(player, "not-in-guild");
            return;
        }
        Guild guild = plugin.getGuilds().get(guildName);
        if (!guild.isLeader(player.getUniqueId())) {
            plugin.getMessageHandler().sendMessage(player, "not-leader");
            return;
        }
        String regionName = args[2];
        double cost;
        try {
            cost = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            plugin.getMessageHandler().sendMessage(player, "invalid-cost");
            return;
        }
        Location location = player.getLocation();
        guild.addWarp(new Warp(regionName, cost, location));
        plugin.getDatabaseManager().saveWarpToDatabase(guildName, regionName, cost, location);
        plugin.getMessageHandler().sendMessage(player, "warp-created", regionName);
    }

    private void handleWarpRemove(Player player, String[] args) {
        if (!plugin.getPermissionManager().hasPermission(player, "gj.guild.warp.remove") && !player.isOp()) {
            plugin.getMessageHandler().sendMessage(player, "no-permission");
            return;
        }
        if (args.length < 3) {
            plugin.getMessageHandler().sendMessage(player, "invalid-warp-remove-command");
            return;
        }
        String guildName = plugin.getPlayerGuild(player.getUniqueId());
        if (guildName == null) {
            plugin.getMessageHandler().sendMessage(player, "not-in-guild");
            return;
        }
        Guild guild = plugin.getGuilds().get(guildName);
        if (!guild.isLeader(player.getUniqueId()) && !plugin.getPermissionManager().hasPermission(player, "gj.guild.admin.remove")) {
            plugin.getMessageHandler().sendMessage(player, "not-leader-or-admin");
            return;
        }
        String regionName = args[2];
        guild.removeWarp(regionName);
        plugin.getDatabaseManager().removeWarpFromDatabase(guildName, regionName);
        plugin.getMessageHandler().sendMessage(player, "warp-removed", regionName);
    }

    private void handleTP(Player player, String[] args) {
        if (args.length < 2) {
            plugin.getMessageHandler().sendMessage(player, "missing-region-name");
            return;
        }
        String guildName = plugin.getPlayerGuild(player.getUniqueId());
        if (guildName == null) {
            plugin.getMessageHandler().sendMessage(player, "not-in-guild");
            return;
        }
        Guild guild = plugin.getGuilds().get(guildName);
        String regionName = args[1];
        Warp warp = guild.getWarp(regionName);
        if (warp != null) {
            double cost = warp.getCost();
            if (guild.getBank() >= cost) {
                BigDecimal bdCost = BigDecimal.valueOf(cost);
                if (plugin.getXcapi().changeNonPlayerBalance(guildName, bdCost, false) == 0) {
                    guild.subtractFromBank(cost);
                    plugin.getDatabaseManager().updateGuildInDatabase(guildName, guild);
                    player.teleport(warp.getLocation());
                    plugin.getMessageHandler().sendMessage(player, "teleported-to-region", regionName);
                } else {
                    plugin.getMessageHandler().sendMessage(player, "deduct-bank-failed");
                }
            } else {
                plugin.getMessageHandler().sendMessage(player, "insufficient-bank-funds");
            }
        } else {
            plugin.getMessageHandler().sendMessage(player, "region-not-found");
        }
    }

    private void handleTPSet(Player player, String[] args) {
        if (!plugin.getPermissionManager().hasPermission(player, "gj.guild.tpset")) {
            plugin.getMessageHandler().sendMessage(player, "no-permission");
            return;
        }
        String guildName = plugin.getPlayerGuild(player.getUniqueId());
        if (guildName == null) {
            plugin.getMessageHandler().sendMessage(player, "not-in-guild");
            return;
        }
        Guild guild = plugin.getGuilds().get(guildName);
        guild.setTpPoint(player.getLocation());
        plugin.getDatabaseManager().saveTpPointToDatabase(guildName, player.getLocation());
        plugin.getMessageHandler().sendMessage(player, "tp-point-set");
    }

    private void handleRemove(Player player, String[] args) {
        if (!plugin.getPermissionManager().hasPermission(player, "gj.guild.admin.remove") && !player.isOp()) {
            plugin.getMessageHandler().sendMessage(player, "no-permission");
            return;
        }
        if (args.length < 2) {
            plugin.getMessageHandler().sendMessage(player, "missing-guild-name");
            return;
        }
        String guildName = args[1];
        if (!plugin.getGuilds().containsKey(guildName)) {
            plugin.getMessageHandler().sendMessage(player, "guild-not-found");
            return;
        }
        Guild guild = plugin.getGuilds().get(guildName);
        if (!guild.isLeader(player.getUniqueId()) && !plugin.getPermissionManager().hasPermission(player, "gj.guild.admin.remove")) {
            plugin.getMessageHandler().sendMessage(player, "not-leader-or-admin");
            return;
        }
        plugin.getGuilds().remove(guildName);
        plugin.getDatabaseManager().removeGuildFromDatabase(guildName);
        plugin.getMessageHandler().sendMessage(player, "guild-removed", guildName);
    }

    private boolean deductPlayerExperience(Player player, int amount) {
        // Placeholder method for deducting player experience
        // Replace this with actual implementation if needed
        plugin.getMessageHandler().sendMessage(player, "experience-deducted", String.valueOf(amount));
        return true; // Return true if deduction was successful
    }
}



