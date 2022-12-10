package me.andyreckt.holiday.bukkit.util.files;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static me.andyreckt.holiday.bukkit.util.files.Locale.FileName.SETTINGS;
import static me.andyreckt.holiday.bukkit.util.files.Locale.FileName.MESSAGES;

@Getter(value = AccessLevel.PRIVATE)
public enum Locale {
    /* MONGO */
    MONGO_HOST(SETTINGS.get(), "mongo.host", "localhost"),
    MONGO_PORT(SETTINGS.get(), "mongo.port", 27017),
    MONGO_AUTH(SETTINGS.get(), "mongo.auth", false),
    MONGO_USERNAME(SETTINGS.get(), "mongo.username", "foo"),
    MONGO_PASSWORD(SETTINGS.get(), "mongo.password", "bar"),
    MONGO_DATABASE(SETTINGS.get(), "mongo.database", "holiday"),

    /* REDIS */
    REDIS_HOST(SETTINGS.get(), "redis.host", "localhost"),
    REDIS_PORT(SETTINGS.get(), "redis.port", 6379),
    REDIS_AUTH(SETTINGS.get(), "redis.auth", false),
    REDIS_PASSWORD(SETTINGS.get(), "redis.password", "bar"),

    /* COLORS */
    COLOR_PRIMARY(SETTINGS.get(), "color.primary", "AQUA"),
    COLOR_SECONDARY(SETTINGS.get(), "color.secondary", "DARK_AQUA"),
    COLOR_CHAT(SETTINGS.get(), "color.chat", "YELLOW"),
    ;


    private final String fileName;
    private final String path;
    @Setter
    private Object def;

    Locale(String fileName, String path, String def) {
        this.fileName = fileName;
        this.path = path;
        this.def = def;
    }

    Locale(String fileName, String path, boolean def) {
        this.fileName = fileName;
        this.path = path;
        this.def = def;
    }

    Locale(String fileName, String path, int def) {
        this.fileName = fileName;
        this.path = path;
        this.def = def;
    }

    Locale(String fileName, String path, double def) {
        this.fileName = fileName;
        this.path = path;
        this.def = def;
    }

    Locale(String fileName, String path, String... def){
        this.fileName = fileName;
        this.path = path;
        this.def = Arrays.asList(def);
    }

    @SneakyThrows
    public static void init(JavaPlugin plugin) {
        for (Locale locale : values()) {
            File file = new File(plugin.getDataFolder(), locale.getFileName());
            if (!file.exists()) {
                plugin.saveResource(locale.getFileName(), false);
            }
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            if (!yamlConfiguration.contains(locale.getPath())) {
                yamlConfiguration.set(locale.getPath(), locale.getDef());
                yamlConfiguration.save(file);
            }
            locale.setDef(yamlConfiguration.get(locale.getPath()));
        }
    }

    public String getString() {
        return (String) this.def;
    }

    public boolean getBoolean() {
        return (boolean) this.def;
    }

    public int getInt() {
        return (int) this.def;
    }

    public double getDouble() {
        return (double) this.def;
    }

    public List<String> getStringList() {
        return (List<String>) this.def;
    }




    enum FileName {
        SETTINGS("settings.yml"),
        MESSAGES("messages.yml");

        private final String fileName;

        FileName(String fileName) {
            this.fileName = fileName;
        }

        public String get() {
            return fileName;
        }
    }
}
