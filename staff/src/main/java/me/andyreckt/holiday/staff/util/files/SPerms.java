package me.andyreckt.holiday.staff.util.files;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter(value = AccessLevel.PRIVATE)
public enum SPerms {

    BUILD("build", "staff.build"),
    SEE_VANISHED("see-vanished", "staff.see"),
    STAFF("staff", "staff.staff"),
    LIST("staff-list", "staff.list"),
    FREEZE("freeze", "staff.freeze"),


    /* OTHER */
    NONE(null, ""),
    OP(null, "op"),
    ;

    private final String path;
    @Setter private String perm;

    SPerms(String path, String perm) {
        this.path = path;
        this.perm = perm;
    }

    public String get() {
        return perm;
    }



    @SneakyThrows
    public static void init(JavaPlugin plugin) {
        for (SPerms perm : values()) {
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
