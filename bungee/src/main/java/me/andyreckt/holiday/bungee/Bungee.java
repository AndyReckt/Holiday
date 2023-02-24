package me.andyreckt.holiday.bungee;

import lombok.Getter;
import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bungee.listener.PermissionsListener;
import me.andyreckt.holiday.bungee.listener.PlayerListener;
import me.andyreckt.holiday.bungee.listener.StaffSwitchListener;
import me.andyreckt.holiday.bungee.tasks.RefreshTask;
import me.andyreckt.holiday.bungee.tasks.OnlinePlayersTask;
import me.andyreckt.holiday.bungee.tasks.ServerCheckerTask;
import me.andyreckt.holiday.bungee.util.Locale;
import me.andyreckt.holiday.core.util.mongo.MongoCredentials;
import me.andyreckt.holiday.core.util.redis.RedisCredentials;
import net.md_5.bungee.api.ChatColor;
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
        if (Locale.MONGO_URI_MODE.getBoolean()) {
            mongoCreds = new MongoCredentials(Locale.MONGO_URI.getString(), Locale.MONGO_DATABASE.getString());
        }
        RedisCredentials redisCreds = new RedisCredentials(Locale.REDIS_HOST.getString(), Locale.REDIS_PORT.getInt(), Locale.REDIS_AUTH.getBoolean(), Locale.REDIS_PASSWORD.getString());
        this.api = API.create(mongoCreds, redisCreds);
        new OnlinePlayersTask();
        new ServerCheckerTask();
        new RefreshTask();
        getProxy().getPluginManager().registerListener(this, new StaffSwitchListener());
        getProxy().getPluginManager().registerListener(this, new PermissionsListener());
        getProxy().getPluginManager().registerListener(this, new PlayerListener());
    }//TODO: MOTD, Maintenance and other stuff

    public String getNameWithColor(Profile profile) {
        IRank rank = profile.getHighestVisibleRank();
        return ChatColor.translateAlternateColorCodes('&', getRankColor(rank) + (rank.isBold() ? "&l" : "") + (rank.isItalic() ? "&o" : "") + profile.getName());
    }

    public ChatColor getRankColor(IRank rank) {
        return ChatColor.valueOf(rank.getColor().toUpperCase());
    }

    public String getServerName(String id) {
        return api.getServer(id).getServerName();
    }
}

