
package de.winniepat.limitlessLevels.services;

import de.winniepat.limitlessLevels.LimitlessLevels;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class EnchantmentService {

    private final LimitlessLevels plugin;

    public EnchantmentService(LimitlessLevels plugin) {
        this.plugin = plugin;
    }

    public boolean enchantHeldItem(Player player, String enchantmentName, int level) {
        if (level < 1 || level > plugin.getMaxLevel()) {
            return false;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir()) {
            return false;
        }

        Enchantment enchantment = findEnchantment(enchantmentName);
        if (enchantment == null) {
            return false;
        }

        item.addUnsafeEnchantment(enchantment, level);
        return true;
    }

    public Enchantment findEnchantment(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);

        if (!normalized.contains(":")) {
            normalized = "minecraft:" + normalized;
        }

        NamespacedKey key = NamespacedKey.fromString(normalized);
        if (key == null) {
            return null;
        }

        return Enchantment.getByKey(key);
    }

    public List<String> getEnchantmentNames() {
        List<String> names = new ArrayList<>();
        for (Enchantment enchantment : Bukkit.getRegistry(Enchantment.class)) {
            NamespacedKey key = enchantment.getKey();
            if (key != null && key.getKey() != null && !names.contains(key.getKey())) {
                names.add(key.getKey());
            }
        }
        Collections.sort(names);
        return names;
    }
}
