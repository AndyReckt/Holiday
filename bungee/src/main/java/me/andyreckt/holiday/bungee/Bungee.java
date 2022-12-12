package me.andyreckt.holiday.bungee;

import lombok.Getter;
import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.bungee.tasks.OnlinePlayersTask;
import me.andyreckt.holiday.bungee.util.Locale;
import me.andyreckt.holiday.core.util.mongo.MongoCredentials;
import me.andyreckt.holiday.core.util.redis.RedisCredentials;
import net.md_5.bungee.api.plugin.Plugin;

@Getter
public class Bungee extends Plugin {

    @Getter
    private static Bungee instance;

    private API api;

    @Override
    public void onEnable() {
        instance = this;
        Locale.init(this);

        MongoCredentials mongoCreds = Locale.MONGO_AUTH.getBoolean() ? new MongoCredentials(
                Locale.MONGO_HOST.getString(), Locale.MONGO_PORT.getInt(), Locale.MONGO_USERNAME.getString(), Locale.MONGO_PASSWORD.getString(), Locale.MONGO_DATABASE.getString())
                : new MongoCredentials(Locale.MONGO_HOST.getString(), Locale.MONGO_PORT.getInt(), Locale.MONGO_DATABASE.getString());
        RedisCredentials redisCreds = new RedisCredentials(Locale.REDIS_HOST.getString(), Locale.REDIS_PORT.getInt(), Locale.REDIS_AUTH.getBoolean(), Locale.REDIS_PASSWORD.getString());
        this.api = API.create(mongoCreds, redisCreds);
        new OnlinePlayersTask();
    }
}

