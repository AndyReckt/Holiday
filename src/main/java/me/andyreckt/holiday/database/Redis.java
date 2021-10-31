package me.andyreckt.holiday.database;

import com.google.gson.JsonObject;
import io.github.zowpy.jedisapi.JedisAPI;
import io.github.zowpy.jedisapi.redis.RedisCredentials;
import lombok.Getter;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.subscibers.PunishmentSubsciber;
import me.andyreckt.holiday.database.subscibers.ServerStartupSubsciber;
import me.andyreckt.holiday.database.utils.RedisUtils;
import org.bukkit.configuration.file.FileConfiguration;

public class Redis {

    @Getter static RedisCredentials credentials;
    @Getter static JedisAPI jedis;

    public Redis() {
        FileConfiguration config = Holiday.getInstance().getConfig();
        credentials = new RedisCredentials(config.getString("Redis.host"), config.getString("Redis.Auth.password"), "Holiday", config.getInt("Redis.port"), config.getBoolean("Redis.Auth.enabled"));
        jedis = new JedisAPI(credentials);
        loadSubscibers();
    }

     void loadSubscibers() {

        jedis.registerSubscriber(new ServerStartupSubsciber());
        jedis.registerSubscriber(new PunishmentSubsciber());

     }

    public static void sendPayload(String type, JsonObject object) {
        RedisUtils.submitToThread(() -> jedis.getJedisHandler().write(type + "###" + object));
    }

}
