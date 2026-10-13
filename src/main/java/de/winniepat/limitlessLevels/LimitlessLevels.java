package de.winniepat.limitlessLevels;

import de.winniepat.limitlessLevels.services.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class LimitlessLevels extends JavaPlugin {

    private EffectService effectService;
    private EnchantmentService enchantmentService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        effectService = new EffectService(this);
        enchantmentService = new EnchantmentService(this);

        PluginCommand command = getCommand("limitlesslevels");
        if (command == null) {
            getLogger().severe("Failed to register command 'limitlesslevels'.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        LimitlessCommand commandExecutor = new LimitlessCommand(this, effectService, enchantmentService);
        command.setExecutor(commandExecutor);
        command.setTabCompleter(commandExecutor);

        getLogger().info("LimitlessLevels has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("LimitlessLevels has been disabled.");
    }

    public int getMaxLevel() {
        return Math.max(1, Math.min(255, getConfig().getInt("max-level", 255)));
    }

    public int getMaxDurationSeconds() {
        return Math.max(1, getConfig().getInt("max-duration-seconds", 1_000_000));
    }

    public EffectService getEffectService() {
        return effectService;
    }

    public EnchantmentService getEnchantmentService() {
        return enchantmentService;
    }
}
