package me.andyreckt.holiday;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.database.mongo.MongoDB;
import me.andyreckt.holiday.database.redis.Redis;
import me.andyreckt.holiday.database.redis.packet.BroadcastPacket;
import me.andyreckt.holiday.listeners.ProfileListener;
import me.andyreckt.holiday.other.enums.BroadcastType;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.player.disguise.DisguiseHandler;
import me.andyreckt.holiday.player.grant.GrantHandler;
import me.andyreckt.holiday.player.punishments.PunishmentHandler;
import me.andyreckt.holiday.player.rank.RankHandler;
import me.andyreckt.holiday.server.ServerHandler;
import me.andyreckt.holiday.utils.StringUtil;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import me.andyreckt.holiday.utils.packets.Pidgin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@Getter @Setter
public final class Holiday extends JavaPlugin {

    @Getter static Holiday instance;

    boolean lunarEnabled = false;
    Gson gson;

    BasicConfigurationFile settings, messages;

    Redis redisPool;
    Pidgin redis;
    JedisPool jedisPool;

    MongoDatabase mongoDatabase;

    PunishmentHandler punishmentHandler;
    ProfileHandler profileHandler;
    DisguiseHandler disguiseHandler;
    RankHandler rankHandler;
    GrantHandler grantHandler;
    ServerHandler serverHandler;

    Executor dbExecutor, executor;


    @Override
    public void onEnable() {
        try {
            this.loadPlugin();
            getLogger().info(ChatColor.GREEN + "Successfully Loaded!");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Plugin was not loaded correctly...");
            e.printStackTrace();
            Bukkit.getServer().shutdown();
        }
    }

    void loadPlugin() {
        instance = this;
        this.gson  = new GsonBuilder().serializeNulls().create();
        setupConfigFiles();
        setupExecutors();
        setupDatabases();
        setupHandlers();
        setupListeners();

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("LunarClient-API")) {
            lunarEnabled = true;
        }

        redis.sendPacket(new BroadcastPacket(StringUtil.addNetworkPlaceholder(
                messages.getString("SERVER.STARTUP")
                        .replace("<server>", settings.getString("SERVER.NAME"))),
                BroadcastType.ADMIN));
    }

    void setupExecutors() {
        this.dbExecutor = Executors.newFixedThreadPool(2);
        this.executor = Executors.newFixedThreadPool(4);
    }


    void setupConfigFiles() {
        this.settings = new BasicConfigurationFile(this, "settings");
        this.messages = new BasicConfigurationFile(this, "messages");
    }

    void setupDatabases() {
        this.mongoDatabase = new MongoDB(settings).getDatabase();
        this.redisPool = new Redis(settings);
        this.jedisPool = redisPool.getJedis();
        this.redis = redisPool.getPidgin();
    }

    void setupHandlers() {
        this.punishmentHandler = new PunishmentHandler();
        this.profileHandler = new ProfileHandler();
        this.rankHandler = new RankHandler();
        this.disguiseHandler = new DisguiseHandler(this);
        this.serverHandler = new ServerHandler(this);
        this.grantHandler = new GrantHandler();
    }


    void setupListeners() {
        addListener(new ProfileListener());
    }
    void addListener(Listener listener){
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        this.redisPool = null;
        this.jedisPool = null;
        this.redis = null;
        this.mongoDatabase = null;
    }
}
