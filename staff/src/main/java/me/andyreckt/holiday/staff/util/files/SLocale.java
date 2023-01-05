package me.andyreckt.holiday.staff.util.files;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.andyreckt.holiday.bukkit.util.text.CC;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static me.andyreckt.holiday.staff.util.files.SLocale.FileName.MESSAGES;
import static me.andyreckt.holiday.staff.util.files.SLocale.FileName.SETTINGS;

@Getter(value = AccessLevel.PRIVATE)
public enum SLocale {

    /* SETTINGS */
    UPDATE_VISIBILITY(SETTINGS.get(),"vanish-update-visibility", true),
    AUTOMATIC_VANISH(SETTINGS.get(),"automatic-vanish", true),


    /* MESSAGES */
    VANISH_ON(MESSAGES.get(), "vanish-on", "&aYou have enabled vanish."),
    VANISH_OFF(MESSAGES.get(), "vanish-off", "&cYou have disabled vanish."),
    STAFF_MOD_DISABLED(MESSAGES.get(), "staff-mode-disabled", "&cYou have disabled staff mode."),
    STAFF_MOD_ENABLED(MESSAGES.get(), "staff-mode-enabled", "&aYou have enabled staff mode."),
    ;


    private final String fileName;
    private final String path;
    @Setter
    private Object def;

    SLocale(String fileName, String path, String def) {
        this.fileName = fileName;
        this.path = path;
        this.def = def;
    }

    SLocale(String fileName, String path, boolean def) {
        this.fileName = fileName;
        this.path = path;
        this.def = def;
    }

    SLocale(String fileName, String path, int def) {
        this.fileName = fileName;
        this.path = path;
        this.def = def;
    }

    SLocale(String fileName, String path, double def) {
        this.fileName = fileName;
        this.path = path;
        this.def = def;
    }

    SLocale(String fileName, String path, String... def){
        this.fileName = fileName;
        this.path = path;
        this.def = Arrays.asList(def);
    }

    @SneakyThrows
    public static void init(JavaPlugin plugin) {
        for (SLocale locale : values()) {
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
        return CC.translate((String) this.def).replace("\\n", "\n").replace("\n", "\n");
    }

    public String getRawString() {
        return (String) this.def;
    }

    public String getStringNetwork() {
        return CC.translate(CC.addNetworkPlaceholder(getString()));
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
