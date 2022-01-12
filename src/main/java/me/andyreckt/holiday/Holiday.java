package me.andyreckt.holiday;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoDatabase;
import io.github.damt.menu.MenuHandler;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.database.mongo.MongoDB;
import me.andyreckt.holiday.database.redis.Redis;
import me.andyreckt.holiday.database.redis.packet.BroadcastPacket;
import me.andyreckt.holiday.database.redis.packet.StaffMessages;
import me.andyreckt.holiday.listeners.ChatListener;
import me.andyreckt.holiday.listeners.ProfileListener;
import me.andyreckt.holiday.listeners.PunishmentsListener;
import me.andyreckt.holiday.other.enums.BroadcastType;
import me.andyreckt.holiday.other.enums.StaffMessageType;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.player.disguise.DisguiseHandler;
import me.andyreckt.holiday.player.grant.GrantHandler;
import me.andyreckt.holiday.player.punishments.PunishmentHandler;
import me.andyreckt.holiday.player.rank.RankHandler;
import me.andyreckt.holiday.server.ServerHandler;
import me.andyreckt.holiday.utils.StringUtil;
import me.andyreckt.holiday.utils.Tasks;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import me.andyreckt.holiday.utils.packets.Pidgin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


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
    ChatHandler chatHandler;
    MenuHandler menuHander;

    Executor dbExecutor, executor;
    ScheduledExecutorService scheduledExecutor;


    @Override
    public void onEnable() {
        try {
            this.loadPlugin();
        } catch (Exception e) {
            infoConsole(ChatColor.DARK_RED + "Plugin was not loaded correctly...");
            e.printStackTrace();
            Bukkit.getServer().shutdown();
        }
    }

    void loadPlugin() {
        instance = this;
        this.gson  = new GsonBuilder().serializeNulls().create();
        this.setupConfigFiles();
        this.setupExecutors();
        this.setupDatabases();
        this.setupHandlers();
        this.setupListeners();
        this.setupRunnables();

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("LunarClient-API")) lunarEnabled = true;

        infoConsole(ChatColor.GREEN + "Successfully Loaded!");
        this.sendServerStartup();
    }

    void sendServerStartup() {
        Tasks.runAsyncLater(() -> redis.sendPacket(new StaffMessages.StaffMessagesPacket(StringUtil.addNetworkPlaceholder(
                messages.getString("SERVER.STARTUP")
                        .replace("<server>", settings.getString("SERVER.NAME"))),
                StaffMessageType.ADMIN)),
                20 * 5L);


    }

    void setupExecutors() {
        this.dbExecutor = Executors.newFixedThreadPool(2);
        this.executor = Executors.newFixedThreadPool(3);
        this.scheduledExecutor = Executors.newScheduledThreadPool(2);
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
        this.chatHandler = new ChatHandler(this.settings, serverHandler.getThisServer());
        this.menuHander = new MenuHandler(this);
    }


    void setupRunnables() {
        Runnable refreshGrants = () -> this.grantHandler.refreshGrants();
        Runnable refreshServer = () -> this.serverHandler.save();

        this.scheduledExecutor.scheduleAtFixedRate(refreshGrants, 0, 1, TimeUnit.MINUTES);
        this.scheduledExecutor.scheduleAtFixedRate(refreshServer, 25, 30, TimeUnit.SECONDS);
    }


    void setupListeners() {
        this.addListener(new ProfileListener());
        this.addListener(new ChatListener());
        this.addListener(new PunishmentsListener());
    }
    void addListener(Listener listener){
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        this.serverHandler.stop();
        this.scheduledExecutor.shutdownNow();
    }

    public void infoConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(CC.translate(message));
    }
}
