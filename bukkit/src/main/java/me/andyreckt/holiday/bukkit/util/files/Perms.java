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
    STAFF_SWITCH("staff.switch", "core.staff.switch"),
    STAFF_SOCIAL_SPY("staff.social-spy", "core.staff.socialspy"),
    STAFF_VIEW_REPORTS("staff.view-reports", "core.staff.reports"),
    STAFF_VIEW_HELPOP("staff.view-helpop", "core.staff.helpop"),

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
    GAMEMODE("command.gamemode", "core.command.gamemode"),
    TELEPORT("command.teleport.player", "core.command.teleport"),
    TELEPORT_ALL("command.teleport.all", "core.command.teleportall"),
    TELEPORT_HERE("command.teleport.here", "core.command.teleporthere"),
    TELEPORT_POSITION("command.teleport.position", "core.command.teleportposition"),
    GRANTS_VIEW("command.grants.view", "core.command.grants.view"),
    GRANTS_EDIT("command.grants.edit", "core.command.grants.edit"),
    REBOOT("command.reboot", "core.command.reboot"),
    RENAME("command.rename", "core.command.rename"),
    GARBAGE("command.garbage", "core.command.garbage"),
    FLY("command.fly", "core.command.fly"),
    GIVE("command.give", "core.command.give"),
    GIVEALL("command.giveall", "core.command.giveall"),
    CRAFT("command.craft", "core.command.craft"),
    ENCHANT("command.enchant", "core.command.enchant"),
    CLEAR("command.clear", "core.command.clear"),
    HEAL("command.heal", "core.command.heal"),
    FEED("command.feed", "core.command.feed"),
    DEMOMODE("command.demomode", "core.command.demomode"),
    BAN("command.ban", "core.command.ban"),
    IPBAN("command.ipban", "core.command.ipban"),
    TEMPBAN("command.tempban", "core.command.tempban"),
    MUTE("command.mute", "core.command.mute"),
    TEMPMUTE("command.tempmute", "core.command.tempmute"),
    KICK("command.kick", "core.command.kick"),
    BLACKLIST("command.blacklist", "core.command.blacklist"),
    UNBAN("command.unban", "core.command.unban"),
    UNIPBAN("command.unipban", "core.command.unipban"),
    UNMUTE("command.unmute", "core.command.unmute"),
    UNBLACKLIST("command.unblacklist", "core.command.unblacklist"),
    KILLALL("command.killall", "core.command.killall"),
    SET_MAX_PLAYERS("command.setmaxplayers", "core.command.slots"),
    MORE("command.more", "core.command.more"),
    SUDO("command.sudo", "core.command.sudo"),
    SUDOALL("command.sudoall", "core.command.sudoall"),
    INVSEE("command.invsee", "core.command.invsee"),
    CHECK_PUNISHMENTS("command.check-punishments", "core.command.checkpunishments"),
    PUNISHMENT_LIST("command.punishment-list", "core.command.punishmentlist"),
    ALTS("command.alts", "core.command.alts"),
    DISGUISE("command.disguise", "core.command.disguise"),
    DISGUISE_MANUAL("command.disguise.manual", "core.command.disguise.manual"),
    DISGUISE_LIST("command.disguise.list", "core.command.disguise.list"),
    DISGUISE_CUSTOM_NAME("command.disguise.choose-name", "core.command.disguise.custom"),
    DISGUISE_RANK("command.disguise.rank", "core.command.disguise.rank"),
    USER("command.user", "core.command.user"),
    USER_WIPE("command.user-wipe", "core.command.user.wipe"),
    USER_RESOLVE("command.user-resolve", "core.command.user.resolve"),
    JOIN("command.join", "core.command.join"),
    PULL("command.pull", "core.command.pull"),
    SEND_TO_SERVER("command.sendtoserver", "core.command.sendtoserver"),
    FIND("command.find", "core.command.find"),
    LAG("command.lag", "core.command.lag"),
    RELOAD("command.reload", "core.command.reload"),

    /* OTHER */
    NONE(null, ""),
    SEE_VANISHED("", "staff.see"),
    OP(null, "op"),

    ;

    private final String path;
    private final String perm;

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
            }
        }
    }

}
