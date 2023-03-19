package me.andyreckt.holiday.bukkit.util.files;

import lombok.Getter;
import me.andyreckt.holiday.bukkit.util.text.CC;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;


/**
 * @author Creaxx
 */

public class ConfigFile extends YamlConfiguration {

    @Getter
    private File file;
    private JavaPlugin plugin;
    private String name;

    public ConfigFile(JavaPlugin plugin, String name) {
        file = new File(plugin.getDataFolder(), name);
        plugin = plugin;
        name = name;

        if (!file.exists()) {
            plugin.saveResource(name, false);
        }

        try {
            this.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public ConfigFile(JavaPlugin plugin, String dataFolder, String name) {
        file = new File(dataFolder, name);
        plugin = plugin;
        name = name;

        try {
            File dFolder = new File(dataFolder);
            Field field = JavaPlugin.class.getDeclaredField("dataFolder");
            field.setAccessible(true);
            field.set(plugin, dFolder);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Could not set dataFolder!");
        }

        if (!file.exists()) {
            plugin.saveResource(name, false);
        }

        try {
            this.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void load(String path) {
        file = new File(path, name);

        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        file = new File(plugin.getDataFolder(), name);

        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getInt(String path) {
        return super.getInt(path, 0);
    }

    @Override
    public double getDouble(String path) {
        return super.getDouble(path, 0.0);
    }

    @Override
    public boolean getBoolean(String path) {
        return super.getBoolean(path, false);
    }

    public String getString(String path, boolean ignored) {
        return super.getString(path, null);
    }

    @Override
    public String getString(String path) {
        return CC.translate(super.getString(path, CC.CHAT + "String at path &7'" + CC.PRIMARY + path + "&7' " + CC.CHAT + "not found."));
    }

    @Override
    public List<String> getStringList(String path) {
        return super.getStringList(path).stream().map(CC::translate).collect(Collectors.toList());
    }

    public List<String> getStringList(String path, boolean ignored) {
        if (!super.contains(path)) return null;
        return super.getStringList(path).stream().map(CC::translate).collect(Collectors.toList());
    }
}