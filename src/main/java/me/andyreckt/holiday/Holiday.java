package me.andyreckt.holiday;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.database.mongo.MongoDB;
import me.andyreckt.holiday.database.redis.Redis;
import me.andyreckt.holiday.database.redis.packets.ServerStartPacket;
import me.andyreckt.holiday.listeners.ProfileListener;
import me.andyreckt.holiday.rank.Rank;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import me.andyreckt.holiday.utils.packets.Pidgin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;


@Getter @Setter
public final class Holiday extends JavaPlugin {

    @Getter static Holiday instance;

    boolean lunarEnabled = false;
    Gson gson;

    BasicConfigurationFile config, messages;

    Redis redisPool;
    Pidgin redis;
    JedisPool jedisPool;

    MongoDatabase mongoDatabase;


    @Override
    public void onEnable() {
        try {
            this.loadPlugin();
            getLogger().info(ChatColor.GREEN + "Successfully Loaded!");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Plugin was not loaded correctly...");
            e.printStackTrace();
        }
    }

    void loadPlugin() {
        instance = this;
        this.gson  = new GsonBuilder().serializeNulls().create();
        setupConfigFiles();



        setupDatabases();



        Rank.init();

        setupListeners();
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("LunarClient-API")) {
            lunarEnabled = true;
        }
        redis.sendPacket(new ServerStartPacket(config.getString("SERVER.NAME")));
    }


    void setupConfigFiles() {
        this.config = new BasicConfigurationFile(this, "settings");
        this.messages = new BasicConfigurationFile(this, "messages");
    }

    void setupDatabases() {
        this.mongoDatabase = new MongoDB(config).getDatabase();
        this.redisPool = new Redis(config);
        this.jedisPool = redisPool.getJedis();
        this.redis = redisPool.getPidgin();
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
