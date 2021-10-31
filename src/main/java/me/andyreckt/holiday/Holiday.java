package me.andyreckt.holiday;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@Getter @Setter
public final class Holiday extends JavaPlugin {

    @Getter static Holiday instance;
    @Getter boolean lunarEnabled = false;
    @Getter static final Gson gson = new GsonBuilder().serializeNulls().create();

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
