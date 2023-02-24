package me.andyreckt.holiday.bungee.util;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static me.andyreckt.holiday.bungee.util.Locale.FileName.MESSAGES;
import static me.andyreckt.holiday.bungee.util.Locale.FileName.SETTINGS;

@Getter(value = AccessLevel.PRIVATE)
public enum Locale {
    /* MONGO */
    MONGO_HOST(SETTINGS.get(), "mongo.host", "localhost"),
    MONGO_PORT(SETTINGS.get(), "mongo.port", 27017),
    MONGO_AUTH(SETTINGS.get(), "mongo.auth", false),
    MONGO_USERNAME(SETTINGS.get(),"mongo.username", "foo"),
    MONGO_PASSWORD(SETTINGS.get(),"mongo.password", "bar"),
    MONGO_DATABASE(SETTINGS.get(),"mongo.database", "holiday"),
    MONGO_URI_MODE(SETTINGS.get(), "mongo.uri-mode", false),
    MONGO_URI(SETTINGS.get(), "mongo.uri", "mongodb://localhost:27017"),

    /* REDIS */
    REDIS_HOST(SETTINGS.get(),"redis.host", "localhost"),
    REDIS_PORT(SETTINGS.get(),"redis.port", 6379),
    REDIS_AUTH(SETTINGS.get(),"redis.auth", false),
    REDIS_PASSWORD(SETTINGS.get(),"redis.password", "bar"),

    /* SERVER INFO */
    SERVER_ID(SETTINGS.get(),"proxy.id", "Proxy-1"),
    SERVER_NAME(SETTINGS.get(),"proxy.name", "Proxy #1"),
    SERVER_AUTO_ADD(SETTINGS.get(),"proxy.auto-server-add", false),

    /* COLORS */
    COLOR_PRIMARY(SETTINGS.get(),"color.primary", "AQUA"),
    COLOR_SECONDARY(SETTINGS.get(),"color.secondary", "DARK_AQUA"),
    COLOR_CHAT(SETTINGS.get(),"color.chat", "YELLOW"),

    /* STAFF */
    STAFF_SWITCH_PERM(MESSAGES.get(),"staff.switch.perm", "core.staff.switch"),
    STAFF_SWITCH_JOIN(MESSAGES.get(),"staff.switch.join", "&b[S] %player% &econnected to %server%."),
    STAFF_SWITCH_LEAVE(MESSAGES.get(),"staff.switch.leave", "&b[S] %player% &edisconnected from %server%."),
    STAFF_SWITCH_SERVER(MESSAGES.get(),"staff.switch.server", "&b[S] %player% &ejoined %server% from %old%."),
    ;

    private final String path;
    private final String filename;

    @Setter
    private Object def;

    Locale(String filename, String path, String def) {
        this.path = path;
        this.def = def;
        this.filename = filename;
    }

    Locale(String filename, String path, boolean def) {
        this.path = path;
        this.def = def;
        this.filename = filename;
    }

    Locale(String filename, String path, int def) {
        this.path = path;
        this.def = def;
        this.filename = filename;
    }

    Locale(String filename, String path, double def) {
        this.path = path;
        this.def = def;
        this.filename = filename;
    }

    Locale(String filename, String path, String... def){
        this.path = path;
        this.def = Arrays.asList(def);
        this.filename = filename;
    }

    @SneakyThrows
    public static void init(Plugin plugin) {
        for (Locale locale : values()) {
            File file = new File(plugin.getDataFolder(), locale.getFilename());

            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }

            Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);

            if (!config.contains(locale.getPath())) {
                config.set(locale.getPath(), locale.getDef());
                YamlConfiguration.getProvider(YamlConfiguration.class).save(config, file);
            }
            locale.setDef(config.get(locale.getPath()));
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
