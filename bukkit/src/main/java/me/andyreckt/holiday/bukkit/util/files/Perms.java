package me.andyreckt.holiday.bukkit.util.files;


import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public enum Perms {
    /* STAFF */



    ;


    private final String path;
    @Setter private String perm;

    Perms(String path, String perm) {
        this.path = path;
        this.perm = perm;
    }

    @SneakyThrows
    public static void init(JavaPlugin plugin) {
        for (Perms locale : values()) {
            File file = new File(plugin.getDataFolder(), "permissions.yml");
            if (!file.exists()) {
                plugin.saveResource("permissions.yml", false);
            }
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            if (!yamlConfiguration.contains(locale.getPath())) {
                yamlConfiguration.set(locale.getPath(), locale.getPerm());
                yamlConfiguration.save(file);
            }
            locale.setPerm(yamlConfiguration.getString(locale.getPath()));
        }
    }

}
