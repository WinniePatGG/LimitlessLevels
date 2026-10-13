package de.winniepat.limitlessLevels.services;

import de.winniepat.limitlessLevels.LimitlessLevels;
import de.winniepat.limitlessLevels.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Locale;

public class EffectService {
    private final LimitlessLevels plugin;
    private final MessageUtil messages;

    public EffectService(LimitlessLevels plugin) {
        this.plugin = plugin;
        this.messages = new MessageUtil(plugin);
    }

    public boolean apply(Player target, String effectName, int durationSeconds, int level) {
        if (level < 1 || level > plugin.getMaxLevel()) {
            return false;
        }

        if (durationSeconds < 1 || durationSeconds > plugin.getMaxDurationSeconds()) {
            return false;
        }

        PotionEffectType type = findEffect(effectName);
        if (type == null) {
            return false;
        }

        int amplifier = level - 1;
        int durationTicks = Math.multiplyExact(durationSeconds, 20);

        PotionEffect effect = new PotionEffect(type, durationSeconds, amplifier, false, true, true);

        return target.addPotionEffect(effect, true);
    }

    public PotionEffectType findEffect(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);

        if (normalized.startsWith("minecraft:")) {
            normalized = normalized.substring("minecraft:".length());
        }

        normalized = normalized.replace('-', '_');

        PotionEffectType type = PotionEffectType.getByName(
                normalized.toUpperCase(Locale.ROOT)
        );

        if (type != null) {
            return type;
        }

        NamespacedKey key = NamespacedKey.fromString(name.toLowerCase(Locale.ROOT));
        if (key == null) {
            return null;
        }

        return Bukkit.getRegistry(PotionEffectType.class).get(key);
    }
}
