package me.andyreckt.holiday;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Holiday extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info(ChatColor.BLUE + "Succesfully Loaded!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
