
package de.winniepat.limitlessLevels;

import de.winniepat.limitlessLevels.services.*;
import de.winniepat.limitlessLevels.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class LimitlessCommand implements CommandExecutor, TabCompleter {

    private final LimitlessLevels plugin;
    private final EffectService effects;
    private final EnchantmentService enchantments;
    private final MessageUtil messages;

    public LimitlessCommand(LimitlessLevels plugin, EffectService effects, EnchantmentService enchantments) {
        this.plugin = plugin;
        this.effects = effects;
        this.enchantments = enchantments;
        this.messages = new MessageUtil(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender, label);
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "effect":
                handleEffect(sender, args);
                break;
            case "enchant":
                handleEnchant(sender, args);
                break;
            case "reload":
                handleReload(sender);
                break;
            default:
                sendUsage(sender, label);
                break;
        }

        return true;
    }

    private void handleEffect(CommandSender sender, String[] args) {
        if (!sender.hasPermission("limitlesslevels.use")) {
            messages.send(sender, "no-permission");
            return;
        }

        if (args.length != 5) {
            sender.sendMessage(MessageUtil.color("&cUsage: /ll effect <player> <effect> <seconds> <level>"));
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            messages.send(sender, "player-not-found");
            return;
        }

        int duration;
        int level;

        try {
            duration = Integer.parseInt(args[3]);
            level = Integer.parseInt(args[4]);
        } catch (NumberFormatException exception) {
            sender.sendMessage(MessageUtil.color(
                    "&cDuration and level must be whole numbers."
            ));
            return;
        }

        if (level < 1 || level > plugin.getMaxLevel()) {
            messages.send(sender, "invalid-level", "max", String.valueOf(plugin.getMaxLevel()));
            return;
        }

        if (duration < 1 || duration > plugin.getMaxDurationSeconds()) {
            messages.send(sender, "invalid-duration");
            return;
        }

        if (effects.findEffect(args[2]) == null) {
            messages.send(sender, "invalid-effect", "value", args[2]);
            return;
        }

        boolean applied = effects.apply(target, args[2], duration, level);

        if (!applied) {
            sender.sendMessage(MessageUtil.color("&cThe effect could not be applied."));
            return;
        }

        messages.send(sender, "effect-applied", "effect", args[2], "level", String.valueOf(level), "player", target.getName(), "duration", String.valueOf(duration));
    }

    private void handleEnchant(CommandSender sender, String[] args) {
        if (!sender.hasPermission("limitlesslevels.use")) {
            messages.send(sender, "no-permission");
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.color("&cOnly players can enchant a held item."));
            return;
        }
        Player player = (Player) sender;

        if (args.length != 3) {
            sender.sendMessage(MessageUtil.color("&cUsage: /ll enchant <enchantment> <level>"));
            return;
        }

        int level;

        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
            sender.sendMessage(MessageUtil.color("&cLevel must be a whole number."));
            return;
        }

        if (level < 1 || level > plugin.getMaxLevel()) {
            messages.send(sender,"invalid-level", "max", String.valueOf(plugin.getMaxLevel()));
            return;
        }

        if (player.getInventory().getItemInMainHand().getType().isAir()) {
            messages.send(sender, "hold-item");
            return;
        }

        if (enchantments.findEnchantment(args[1]) == null) {
            messages.send(sender, "invalid-enchantment", "value", args[1]);
            return;
        }

        if (!enchantments.enchantHeldItem(player, args[1], level)) {
            sender.sendMessage(MessageUtil.color("&cThe enchantment could not be applied."));
            return;
        }

        messages.send(sender,"enchant-applied", "enchantment", args[1], "level", String.valueOf(level));
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("limitlesslevels.reload")) {
            messages.send(sender, "no-permission");
            return;
        }

        plugin.reloadConfig();
        messages.send(sender, "reloaded");
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("limitlesslevels.use")) StringUtil.copyPartialMatches(args[0], Arrays.asList("effect", "enchant"), completions);
            if (sender.hasPermission("limitlesslevels.reload")) StringUtil.copyPartialMatches(args[0], Arrays.asList("reload"), completions);

            Collections.sort(completions);
            return completions;
        }

        String subcommand = args[0].toLowerCase(Locale.ROOT);

        if (subcommand.equals("effect")) {
            if (!sender.hasPermission("limitlesslevels.use")) return completions;

            if (args.length == 2) {
                List<String> players = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    players.add(player.getName());
                }
                return filter(players, args[1]);
            }

            if (args.length == 3) return filter(effects.getEffectNames(), args[2]);

            return completions;
        }

        if (subcommand.equals("enchant")) {
            if (!sender.hasPermission("limitlesslevels.use")) return completions;
            if (args.length == 2) return filter(enchantments.getEnchantmentNames(), args[1]);

            return completions;
        }

        return completions;
    }

    private List<String> filter(List<String> options, String token) {
        List<String> result = new ArrayList<>();
        StringUtil.copyPartialMatches(token == null ? "" : token, options, result);
        Collections.sort(result);
        return result;
    }

    private void sendUsage(CommandSender sender, String label) {
        sender.sendMessage(MessageUtil.color("&aLimitlessLevels &7- commands"));
        sender.sendMessage(MessageUtil.color("&e/" + label + " effect <player> <effect> <seconds> <level>"));
        sender.sendMessage(MessageUtil.color("&e/" + label + " enchant <enchantment> <level>"));
        sender.sendMessage(MessageUtil.color("&e/" + label + " reload"));
    }
}
