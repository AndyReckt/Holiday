package me.andyreckt.holiday.bukkit.util;

import me.andyreckt.holiday.bukkit.util.files.Locale;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {

    public static boolean DEV = false;

    public static void init() {
        DEV = Locale.DEV_MODE.getBoolean();
    }

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[Holiday] " + message));
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[Holiday] [ERROR] " + message));
    }

    public static void debug(String message) {
        if (!DEV) return;
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[Holiday] [DEBUG] " + message));
    }

    public static void warn(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Holiday] [WARN] " + message));
    }

    public static void severe(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&4[Holiday] [SEVERE] " + message));
        Bukkit.shutdown();
    }

}