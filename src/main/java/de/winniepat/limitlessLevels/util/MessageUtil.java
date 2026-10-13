package de.winniepat.limitlessLevels.util;

import de.winniepat.limitlessLevels.LimitlessLevels;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtil {

    private final LimitlessLevels plugin;

    public MessageUtil(LimitlessLevels plugin) {
        this.plugin = plugin;
    }

    public void send(CommandSender sender, String key) {
        String prefix = plugin.getConfig().getString("messages.prefix", "&8[&aLimitlessLevels&8] &r");
        String message = plugin.getConfig().getString("messages." + key, "&cMissing message: " + key);

        sender.sendMessage(color(prefix + message));
    }

    public void send(CommandSender sender, String key, String... replacements) {
        String prefix = plugin.getConfig().getString(
                "messages.prefix", "&8[&aLimitlessLevels&8] &r"
        );

        String message = plugin.getConfig().getString(
                "messages." + key, "&cMissing message: " + key
        );

        for (int i = 0;  i + 1 < replacements.length; i += 2) {
            message = message.replace("%" + replacements[i] + "%", replacements[i + 1]);
        }

        sender.sendMessage(color(prefix + message));
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
