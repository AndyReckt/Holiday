package me.andyreckt.holiday.bukkit.util.files;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter(value = AccessLevel.PRIVATE)
public enum Perms {
    /* STAFF */
    STAFF_VIEW_FILTERED_MESSAGES("staff.view-filtered-messages", "core.staff.filtered"),

    /* PUNISHMENTS */
    PUNISHMENTS_SILENT_VIEW("punishments.silent-view", "core.punishments.silent"),

    /* OTHER */
    RANKS("command.ranks", "core.command.ranks"),
    DEBUG("command.debug", "core.command.debug"),

    NONE(null, ""),
    OP(null, "op"),
    ;


    private final String path;
    @Setter private String perm;

    Perms(String path, String perm) {
        this.path = path;
        this.perm = perm;
    }

    public String get() {
        return perm;
    }



    @SneakyThrows
    public static void init(JavaPlugin plugin) {
        for (Perms perm : values()) {
            File file = new File(plugin.getDataFolder(), "permissions.yml");
            if (!file.exists()) {
                plugin.saveResource("permissions.yml", false);
            }
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            if (!(perm.getPath() == null)) {
                if (!yamlConfiguration.contains(perm.getPath())) {
                    yamlConfiguration.set(perm.getPath(), perm.getPerm());
                    yamlConfiguration.save(file);
                }
                perm.setPerm(yamlConfiguration.getString(perm.getPath()));
            }
        }
    }

}
