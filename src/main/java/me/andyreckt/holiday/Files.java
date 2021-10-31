package me.andyreckt.holiday;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.FileManagement;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;


import java.util.*;

@SuppressWarnings({"unchecked", "unused"}) public class Files {

    @AllArgsConstructor
    public enum Config {
        /* REDIS MANAGER */
        REDIS_HOSTNAME("REDIS.HOSTNAME", "localhost"),
        REDIS_PASSWORD("REDIS.PASSWORD", ""),
        REDIS_PORT("REDIS.PORT", 6379),
        REDIS_AUTH("REDIS.AUTH", true),

        /* SERVER INFORMATIONS */
        SERVER_NAME("SERVERNAME", "Lobby"),
        NETWORK_NAME("NETWORKNAME", "example network"),
        NETWORK_IP("NETWORKIP", "example.com"),
        NETWORK_WEBSITE("NETWORKWEBSITE", "https://example.com"),
        NETWORK_STORE("NETWORKSTORE", "https://store.example.com"),
        NETWORK_DISCORD("NETWORKDISCORD", "https://discord.example.com"),
        NETWORK_TWITTER("NETWORKSTORE", "https://twitter.com/examplenetwork"),
        NETWORK_TEAMSPEAK("NETWORKTEAMSPEAK", "ts.example.com"),




        /* MONGO MANAGER */
        MONGO_URI("MONGO.URI", "mongodb://localhost:27017");

        @Getter final String path;
         final Object object;

        @Getter final FileConfiguration config = FileManagement.CONFIG.getConfig();

        public static void createValues(Plugin plugin) {
            FileConfiguration config = FileManagement.CONFIG.getConfig();
            Arrays.stream(values()).filter(v -> config.get(v.getPath()) != null).forEach(v -> config.set(v.getPath(), v.getObject()));
        }

        public Object getObject() {
            if(config.get(this.getPath()) != null) return config.get(this.getPath());
            return object;
        }
        public String getString() {
            if(config.get(this.getPath()) != null) return CC.translate(config.getString(this.getPath()));
            if(object instanceof String) return CC.translate((String) object); else return "";
        }
        public Boolean getValue() {
            if(config.get(this.getPath()) != null) return config.getBoolean(this.getPath());
            if(object instanceof Boolean) return (Boolean) object; else return false;
        }
        public Integer getInteger() {
            if(config.get(this.getPath()) != null) return config.getInt(this.getPath());
            if(object instanceof Integer) return (Integer) object; else return 0;
        }
        public Float getFloat() {
            if(config.get(this.getPath()) != null) return config.getFloat(this.getPath()); if(object instanceof Float) return (Float) object; else return 0F;
        }
        public Double getDouble() {
            if(config.get(this.getPath()) != null) return config.getDouble(this.getPath()); if(object instanceof Double) return (Double) object; else return 0D;
        }
        public List<String> getCollection() {
            if(config.get(this.getPath()) != null) return config.getStringList(this.getPath());
            if(object instanceof ArrayList) return (ArrayList<String>) object; else return new ArrayList<>();
        }

    }

    @AllArgsConstructor
    public enum Messages {
        /* BAN MESSAGES */
        SILENT_PREFIX("PUNISHMENTS.SILENTPREFIX", "&7(Silent) "),
        BAN_MESSAGE("PUNISHMENTS.BANMESSAGE", "&c<player> &chas been permanently banned by <executor> &cfor <reason>"),
        IP_BAN_MESSAGE("PUNISHMENTS.IPBANMESSAGE", "&c<player> &chas been permanently ip-banned by <executor> &cfor <reason>"),
        BLACKLIST_MESSATE("PUNISHMENTS.BLACKLISTMESSAGE", "&c<player> &chas been blacklisted by <executor> &cfor <reason>"),
        UNBAN_MESSAGE("PUNISHMENTS.UNBANMESSAGE", "&a<player> &ahas been unbanned by <executor> for &a<reason>"),
        UNIPBAN_MESSAGE("PUNISHMENTS.UNIPBANMESSAGE", "&a<player> &ahas been unip-banned by <executor> &afor <reason>"),
        UNBLACKLIST_MESSAGE("PUNISHMENTS.UNBLACKLISTMESSAGE", "&a<player> &ahas been unblacklisted by <executor> &afor <reason>"),
        MUTE_MESSAGE("PUNISHMENTS.MUTEMESSAGE", "&c<player> &chas been muted by <executor> &cfor <reason>"),
        UNMUTE_MESSAGE("PUNISHMENTS.UNMUTEMESSAGE", "&a<player> &ahas been unmuted by <executor> &afor <reason>"),
        WARN_MESSAGE("PUNISHMENTS.WARNMESSAGE", "&c<player> &chas been warned by <executor> &cfor <reason>"),
        UNWARNED_MESSAGE("PUNISHMENTS.UNWARNMESSAGE", "&a<executor> removed a warn of <player> &afor <reason>"),
        KICK_MESSAGE("PUNISHMENTS.KICKMESSAGE", "&c<executor> kicked <player> &cfor <reason>"),
        TEMP_MUTE_MESSAGE("PUNISHMENTS.TEMPMUTEMESSAGE", "&c<player> &chas been temporarly muted by <executor> &cfor <reason> for a duration of <duration>"),
        TEMP_BAN_MESSAGE("PUNISHMENTS.TEMPBANMESSAGE", "&c<player> &chas been unblacklisted by <executor> &cfor <reason> for a duration of <duration>"),

        /* BAN JOIN MESSAGES */
        BAN_JOIN("PUNISHMENTS.JOIN.BANNED", "&cYou have been permanently banned from <network_name> for <reason>. \n &cAppeal at <network_discord> or buy an unban at <network_store>"),
        TEMP_BAN_JOIN("PUNISHMENTS.JOIN.TEMPBANNED", "&cYou have been temporarly banned from <network_name> for a duration of <duration> for <reason>. \n &cAppeal at <network_discord> or buy an unban at <network_store>"),
        IP_BAN_JOIN("PUNISHMENTS.JOIN.IPBANNED", "&cYou have been permanently ip-banned from <network_name>. \n &cAppeal at <network_discord> or buy an unban at <network_store>"),
        BLACKLIST_JOIN("PUNISHMENTS.JOIN.BLACKLISTED", "&cYou have been blacklisted from <network_name> for <reason>. \n &cThis type of punishment can't be appealed");




        @Getter final String path;
        final Object object;

        @Getter final FileConfiguration config = FileManagement.CONFIG.getConfig();

        public static void createValues(Plugin plugin) {
            FileConfiguration config = FileManagement.CONFIG.getConfig();
            Arrays.stream(values()).filter(v -> config.get(v.getPath()) != null).forEach(v -> config.set(v.getPath(), v.getObject()));
        }

        public Object getObject() {
            if(config.get(this.getPath()) != null) return config.get(this.getPath());
            return object;
        }
        public String getString() {
            if(config.get(this.getPath()) != null) return CC.translate(config.getString(this.getPath()));
            if(object instanceof String) return CC.translate((String) object); else return "";
        }
        public Boolean getValue() {
            if(config.get(this.getPath()) != null) return config.getBoolean(this.getPath());
            if(object instanceof Boolean) return (Boolean) object; else return false;
        }
        public Integer getInteger() {
            if(config.get(this.getPath()) != null) return config.getInt(this.getPath());
            if(object instanceof Integer) return (Integer) object; else return 0;
        }
        public Float getFloat() {
            if(config.get(this.getPath()) != null) return config.getFloat(this.getPath()); if(object instanceof Float) return (Float) object; else return 0F;
        }
        public Double getDouble() {
            if(config.get(this.getPath()) != null) return config.getDouble(this.getPath()); if(object instanceof Double) return (Double) object; else return 0D;
        }
        public List<String> getCollection() {
            if(config.get(this.getPath()) != null) return config.getStringList(this.getPath());
            if(object instanceof ArrayList) return (ArrayList<String>) object; else return new ArrayList<>();
        }

    }

}
