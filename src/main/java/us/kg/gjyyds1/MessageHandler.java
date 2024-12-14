package us.kg.gjyyds1;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MessageHandler {

    private ConfigProcess configProcess;
    private Map<String, String> messages;

    public MessageHandler(ConfigProcess configProcess) {
        this.configProcess = configProcess;
        this.messages = new HashMap<>();
        loadMessages();
    }

    private void loadMessages() {
        messages.put("not-player", "&c只有玩家才能执行此命令！");
        messages.put("unknown-command", "&c未知子命令: %s");
        messages.put("command-create", "&e/guild create <公会名称>");
        messages.put("command-join", "&e/guild join <公会名称>");
        messages.put("command-save", "&e/guild save exp/money <数量>/all");
        messages.put("command-pvp", "&e/guild pvp join/exit");
        messages.put("command-warp", "&e/guild warp create <区域名称> <进入时需要扣除的金额>");
        messages.put("command-tp", "&e/guild tp <区域名称>");
        messages.put("command-tpset", "&e/guild tpset");
        messages.put("command-remove", "&e/guild remove <公会名称>");
        messages.put("no-permission", "&c你没有权限执行此命令！");
        messages.put("missing-guild-name", "&c请输入公会名称！");
        messages.put("guild-exists", "&c该公会已经存在！");
        messages.put("guild-created", "&a成功创建公会: %s");
        messages.put("guild-not-found", "&c该公会不存在！");
        messages.put("joined-guild", "&a成功加入公会: %s");
        messages.put("invalid-save-command", "&c使用方法: /guild save exp/money <数量>/all");
        messages.put("invalid-number", "&c请输入有效的数字！");
        messages.put("not-in-guild", "&c你不在任何公会中！");
        messages.put("exp-saved", "&a成功保存经验值：%s");
        messages.put("deduct-exp-failed", "&c扣除经验值失败！");
        messages.put("money-deducted", "&a成功扣除公会资金：%s");
        messages.put("deduct-money-failed", "&c扣除公会资金失败！");
        messages.put("resources-saved", "&a成功保存全部资源");
        messages.put("unknown-type", "&c未知类型：%s");
        messages.put("invalid-pvp-command", "&c使用方法: /guild pvp join/exit");
        messages.put("joined-pvp", "&a成功加入PVP擂台");
        messages.put("left-pvp", "&a成功离开PVP擂台");
        messages.put("missing-subcommand", "&c请输入子命令！");
        messages.put("unknown-subcommand", "&c未知子命令: %s");
        messages.put("invalid-warp-create-command", "&c使用方法: /guild warp create <区域名称> <进入时需要扣除的金额>");
        messages.put("not-leader", "&c你不是公会会长！");
        messages.put("invalid-cost", "&c请输入有效的金额！");
        messages.put("warp-created", "&a成功创建公会区域: %s");
        messages.put("invalid-warp-remove-command", "&c使用方法: /guild warp remove <区域名称>");
        messages.put("not-leader-or-admin", "&c你不是公会会长且没有管理权限！");
        messages.put("warp-removed", "&a成功删除公会区域: %s");
        messages.put("missing-region-name", "&c请输入区域名称！");
        messages.put("teleported-to-region", "&a成功传送到区域: %s");
        messages.put("deduct-bank-failed", "&c扣除公会资金失败！");
        messages.put("insufficient-bank-funds", "&c公会资金不足，无法传送！");
        messages.put("region-not-found", "&c该区域不存在！");
        messages.put("tp-point-set", "&a成功设置传送点");
        messages.put("guild-removed", "&a成功删除公会: %s");
        messages.put("experience-deducted", "&7从你的经验中扣除了 %s 点经验。");
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "&c未知消息键：" + key);
    }

    public String getMessage(String key, Object... placeholders) {
        String message = getMessage(key);
        for (int i = 0; i < placeholders.length; i++) {
            message = message.replace("%" + (i + 1), placeholders[i].toString());
        }
        return message;
    }

    public void sendMessage(Player player, String key) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage(key)));
    }

    public void sendMessage(Player player, String key, Object... placeholders) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage(key, placeholders)));
    }

    public void sendActionBar(Player player, String key) {
        BaseComponent[] actionbar = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', getMessage(key)));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionbar);
    }

    public void sendActionBar(Player player, String key, Object... placeholders) {
        BaseComponent[] actionbar = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', getMessage(key, placeholders)));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionbar);
    }
}



