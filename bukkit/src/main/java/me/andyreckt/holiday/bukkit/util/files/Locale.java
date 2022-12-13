package me.andyreckt.holiday.bukkit.util.files;


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

import static me.andyreckt.holiday.bukkit.util.files.Locale.FileName.MESSAGES;
import static me.andyreckt.holiday.bukkit.util.files.Locale.FileName.SETTINGS;

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

    /* SERVER */
    SERVER_ID(SETTINGS.get(), "server.bungee-name", "$undefined"),
    SERVER_NAME(SETTINGS.get(), "server.name", "Unknown"),
    BANNED_JOIN(SETTINGS.get(), "server.banned-join", false),

    /* COLORS */
    COLOR_PRIMARY(SETTINGS.get(), "color.primary", "AQUA"),
    COLOR_SECONDARY(SETTINGS.get(), "color.secondary", "DARK_AQUA"),
    COLOR_CHAT(SETTINGS.get(), "color.chat", "YELLOW"),

    /* FILTERS */
    FILTER_SEND(SETTINGS.get(), "filter.send-to-staff", true),
    FILTER_MESSAGE(SETTINGS.get(), "filter.message", "&d[Filtered] &5[%server%] %player%&e: %message%"),
    FILTER_LOW_ALLOW(SETTINGS.get(), "filter.low.allow-message", true),
    FILTER_LOW_LIST(SETTINGS.get(), "filter.low.list", "cunt", "anal", "beaner", "nazi", "paki"),
    FILTER_HIGH_ALLOW(SETTINGS.get(), "filter.high.allow-message", false),
    FILTER_HIGH_MUTE(SETTINGS.get(), "filter.high.mute", true),
    FILTER_HIGH_MUTE_DURATION(SETTINGS.get(), "filter.high.mute-duration", "3h"),
    FILTER_HIGH_MUTE_REASON(SETTINGS.get(), "filter.high.mute-reason", "Automute (%word%)"),
    FILTER_HIGH_LIST(SETTINGS.get(), "filter.high.list", "nigger", "nigga", "coon", "faggot"),

    /* NETWORK */
    NETWORK_NAME(SETTINGS.get(), "network.name", "example network"),
    NETWORK_IP(SETTINGS.get(), "network.ip", "example.com"),
    NETWORK_WEBSITE(SETTINGS.get(), "network.website", "https://example.com"),
    NETWORK_STORE(SETTINGS.get(), "network.store", "https://store.example.com"),
    NETWORK_DISCORD(SETTINGS.get(), "network.discord", "https://discord.example.com"),
    NETWORK_TWITTER(SETTINGS.get(), "network.twitter", "https://twitter.com/examplenetwork"),
    NETWORK_TEAMSPEAK(SETTINGS.get(), "network.teamspeak", "ts.example.com"),


    /* PUNISHMENTS */
    PUNISHMENT_SILENT_PREFIX(MESSAGES.get(), "punishment.silent-prefix", "&7(Silent) "),
    PUNISHMENT_MUTE_MESSAGE(MESSAGES.get(), "punishment.mute.message", "%silent%&c%player% &chas been muted by %executor% &cfor %reason%."),
    PUNISHMENT_MUTE_PLAYER(MESSAGES.get(), "punishment.mute.player", "&cYou are muted, this mute is permanent."),
    PUNISHMENT_TEMP_MUTE_MESSAGE(MESSAGES.get(), "punishment.temp-mute.message", "%silent%&c%player% &chas been muted by %executor% &cfor %reason%."),
    PUNISHMENT_TEMP_MUTE_PLAYER(MESSAGES.get(), "punishment.temp-mute.player", "&cYou are muted, this mute will expire in %duration%."),
    PUNISHMENT_BAN_MESSAGE(MESSAGES.get(), "punishment.ban.message", "%silent%&c%player% &chas been permanently banned by %executor% &cfor %reason%."),
    PUNISHMENT_BAN_KICK(MESSAGES.get(), "punishment.ban.kick-message", "&cYou have been permanently banned from %network_name% for %reason%. \\n &cAppeal at %discord% or buy an unban at %store%."),
    PUNISHMENT_TEMP_BAN_MESSAGE(MESSAGES.get(), "punishment.temp-ban.message", "%silent%&c%player% &chas been temporarily banned by %executor% &cfor %reason% for a duration of %duration%."),
    PUNISHMENT_TEMP_BAN_KICK(MESSAGES.get(), "punishment.temp-ban.kick-message", "&cYou have been temporarily banned from %network_name% for a duration of %duration% for %reason%. \\n &cAppeal at %discord% or buy an unban at %store%."),
    PUNISHMENT_IP_BAN_MESSAGE(MESSAGES.get(), "punishment.ip-ban.message", "%silent%&c%player% &chas been permanently ip-banned by %executor% &cfor %reason%."),
    PUNISHMENT_IP_BAN_KICK(MESSAGES.get(), "punishment.ip-ban.kick-message", "&cYou have been permanently ip-banned from %network_name% for %reason%. \\n &cAppeal at %discord% or buy an unban at %store%."),
    PUNISHMENT_BLACKLIST_MESSAGE(MESSAGES.get(), "punishment.ban.message", "%silent%&c%player% &chas been blacklisted by %executor% &cfor %reason%."),
    PUNISHMENT_BLACKLIST_KICK(MESSAGES.get(), "punishment.blacklist.kick-message", "&cYou have been blacklisted from %network_name% for %reason%. \\n &cThis type of punishment cannot be appealed."),
    PUNISHMENT_BAN_REVOKED(MESSAGES.get(), "punishment.revoked.ban", "%silent%&a%player% &ahas been unbanned by %executor% &afor %reason%."),
    PUNISHMENT_MUTE_REVOKED(MESSAGES.get(), "punishment.revoked.mute", "%silent%&a%player% &ahas been unmuted by %executor% &afor %reason%."),
    PUNISHMENT_BLACKLIST_REVOKED(MESSAGES.get(), "punishment.revoked.blacklist", "%silent%&a%player% &ahas been unblacklisted by %executor% &afor %reason%."),

    /* RANKS */
    RANK_SUCCESSFULLY_CREATED(MESSAGES.get(), "rank.successfully-created", "&aSuccessfully created rank %rank%."),
    RANK_SUCCESSFULLY_DELETED(MESSAGES.get(), "rank.successfully-deleted", "&aSuccessfully deleted rank %rank%."),
    RANK_PERMISSION_ADDED(MESSAGES.get(), "rank.permission-added", "&aSuccessfully added permission '%permission%' to rank %rank%."),
    RANK_PERMISSION_REMOVED(MESSAGES.get(), "rank.permission-removed", "&aSuccessfully removed permission '%permission%' from rank %rank%."),
    RANK_INHERITANCE_ADDED(MESSAGES.get(), "rank.inheritance-added", "&aSuccessfully added inheritance '%child%' to rank %rank%."),
    RANK_INHERITANCE_REMOVED(MESSAGES.get(), "rank.inheritance-removed", "&aSuccessfully removed inheritance '%child%' from rank %rank%."),
    RANK_PRIORITY_SET(MESSAGES.get(), "rank.priority-set", "&aSuccessfully set priority of rank %rank% to %priority%."),
    RANK_ENTER_NAME(MESSAGES.get(), "rank.edit-name", "&aPlease enter the new name of the rank."),
    RANK_ENTER_NAME_SUCCESS(MESSAGES.get(), "rank.edit-name-success", "&aSuccessfully changed the name of the rank to %name%."),
    RANK_ENTER_DISPLAY_NAME(MESSAGES.get(), "rank.edit-display-name", "&aPlease enter the new display name of the rank."),
    RANK_ENTER_DISPLAY_NAME_SUCCESS(MESSAGES.get(), "rank.edit-display-name-success", "&aSuccessfully changed the display name of the rank to %name%."),
    RANK_ENTER_PREFIX(MESSAGES.get(), "rank.edit-prefix", "&aPlease enter the new prefix of the rank."),
    RANK_ENTER_PREFIX_SUCCESS(MESSAGES.get(), "rank.edit-prefix-success", "&aSuccessfully changed the prefix of the rank to %prefix%."),
    RANK_ENTER_SUFFIX(MESSAGES.get(), "rank.edit-suffix", "&aPlease enter the new suffix of the rank."),
    RANK_ENTER_SUFFIX_SUCCESS(MESSAGES.get(), "rank.edit-suffix-success", "&aSuccessfully changed the suffix of the rank to %suffix%."),
    RANK_COLOR_UPDATED(MESSAGES.get(), "rank.color-updated", "&aSuccessfully updated the color of the rank to %color%."),



    /* ERROR */
    NO_PERMISSION(MESSAGES.get(), "error.no-permission", "&cYou do not have permission to execute this command."),
    RANK_ALREADY_EXISTS(MESSAGES.get(), "error.rank-already-exists", "&cA rank with that name already exists."),
    RANK_NOT_FOUND(MESSAGES.get(), "error.rank-not-found", "&cA rank with that name could not be found."),
    RANK_PERMISSION_ALREADY_EXISTS(MESSAGES.get(), "error.rank-permission-already-exists", "&cRank %rank% already has permission '%permission%'."),
    RANK_PERMISSION_DOES_NOT_EXIST(MESSAGES.get(), "error.rank-permission-does-not-exist", "&cRank %rank% does not have permission '%permission%'."),
    RANK_INHERITANCE_ALREADY_EXISTS(MESSAGES.get(), "error.rank-inheritance-already-exists", "&cRank %rank% already inherits from rank %child%."),
    RANK_INHERITANCE_DOES_NOT_EXIST(MESSAGES.get(), "error.rank-inheritance-does-not-exist", "&cRank %rank% does not inherit from rank %child%."),
    CANNOT_DELETE_DEFAULT_RANK(MESSAGES.get(), "error.cannot-delete-default-rank", "&cYou cannot delete the default rank."),
    NEED_NAME(MESSAGES.get(), "error.need-name", "&cYou need to specify a name."),
    PLAYER_NOT_FOUND(MESSAGES.get(), "error.player-not-found", "&cA player with that name could not be found."),

    /* OTHER */
    DEV_MODE(SETTINGS.get(), "dev-mode", false),
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
        return CC.translate((String) this.def);
    }

    public String getStringNetwork() {
        return CC.translate(CC.addNetworkPlaceholder((String) this.def));
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
