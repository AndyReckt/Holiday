package me.andyreckt.holiday;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;


@Getter @Setter
public final class Holiday extends JavaPlugin {
    Holiday Instance;

    @Override
    public void onEnable() {
        Instance = this;
        saveDefaultConfig();
        getLogger().info(ChatColor.BLUE + "Successfully Loaded!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
