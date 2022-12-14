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
    STAFF_CHAT("staff.staff-chat", "core.staff.chat"),
    STAFF_VIEW_NOTIFICATIONS("staff.view-notifications", "core.staff.notifications"),
    STAFF_CHAT_BYPASS("staff.chat-bypass", "core.chat.bypass"),
    STAFF_WHITELIST_BYPASS("staff.whitelist-bypass", "core.whitelist.bypass"),

    /* ADMIN */
    ADMIN_CHAT("admin.admin-chat", "core.admin.chat"),
    ADMIN_VIEW_NOTIFICATIONS("admin.view-notifications", "core.admin.notifications"),

    /* PUNISHMENTS */
    PUNISHMENTS_SILENT_VIEW("punishments.silent-view", "core.punishments.silent"),

    /* COMMANDS */
    RANKS("command.ranks", "core.command.ranks"),
    DEBUG("command.debug", "core.command.debug"),
    CHAT("command.chat.help", "core.command.chat"),
    CHAT_CLEAR("command.chat.clear", "core.command.chat.clear"),
    CHAT_MUTE("command.chat.mute", "core.command.chat.mute"),
    CHAT_SLOW("command.chat.slow", "core.command.chat.slow"),
    WHITELIST("command.whitelist", "core.command.whitelist"),
    SERVERMANAGER("command.servermanager", "core.command.servermanager"),


    /* OTHER */
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
