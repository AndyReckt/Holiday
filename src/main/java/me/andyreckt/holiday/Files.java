package me.andyreckt.holiday;

import lombok.AllArgsConstructor;
import lombok.Getter;
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
        NETWORK_IP("NETWORKNAME", "example.com"),
        NETWORK_WEBSITE("NETWORKNAME", "https://example.com"),

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
            if(config.get(this.getPath()) != null) return config.getString(this.getPath());
            if(object instanceof String) return (String) object; else return "";
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
