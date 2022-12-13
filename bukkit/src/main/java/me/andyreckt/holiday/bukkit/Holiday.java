package me.andyreckt.holiday.bukkit;

import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.commands.DebugCommand;
import me.andyreckt.holiday.bukkit.commands.RankCommand;
import me.andyreckt.holiday.bukkit.server.chat.ChatManager;
import me.andyreckt.holiday.bukkit.server.listeners.PlayerListener;
import me.andyreckt.holiday.bukkit.server.nms.INMS;
import me.andyreckt.holiday.bukkit.server.nms.impl.NMS_v1_7;
import me.andyreckt.holiday.bukkit.server.nms.impl.NMS_v1_8;
import me.andyreckt.holiday.bukkit.server.redis.subscriber.BroadcastSubscriber;
import me.andyreckt.holiday.bukkit.server.tasks.ServerTask;
import me.andyreckt.holiday.bukkit.util.Logger;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.menu.MenuAPI;
import me.andyreckt.holiday.bukkit.util.other.Tasks;
import me.andyreckt.holiday.bukkit.util.sunset.Sunset;
import me.andyreckt.holiday.bukkit.util.sunset.parameter.custom.ProfileParameterType;
import me.andyreckt.holiday.bukkit.util.sunset.parameter.custom.RankParameterType;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.uuid.UUIDCache;
import me.andyreckt.holiday.core.server.Server;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import me.andyreckt.holiday.core.util.mongo.MongoCredentials;
import me.andyreckt.holiday.core.util.redis.RedisCredentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;

@Getter
public final class Holiday extends JavaPlugin implements Listener {

    @Getter
    private static Holiday instance;

    private API api;

    private INMS nms;
    private Sunset commandManager;
    private MenuAPI menuAPI;
    private ChatManager chatManager;

    private UUIDCache uuidCache;

    private Executor executor;
    private ScheduledExecutorService scheduledExecutor;

    @Setter
    private boolean joinable = false;

    private Server thisServer;

    private ServerTask serverTask;


    @Override
    public void onEnable() {
        instance = this;
        long time = System.currentTimeMillis();

        try {
            this.setupNms();
            this.setupConfigFiles();
            this.setupApi();

            this.setupExecutors();
            this.setupManagers();
            this.setupTasks();
            this.setupListeners();
            this.setupCommands();
            this.setupSoftDependencies();
            this.setupOthers();

            logInformation(time);
        } catch (Exception ex) {
            Logger.error("An error occurred while enabling the plugin. Showing stacktrace:");
            ex.printStackTrace();
            Logger.error("Stopping the server...");
            Bukkit.getServer().shutdown();
        }
    }

    private void setupApi() {
        MongoCredentials mongoCreds = Locale.MONGO_AUTH.getBoolean() ? new MongoCredentials(
                Locale.MONGO_HOST.getString(), Locale.MONGO_PORT.getInt(), Locale.MONGO_USERNAME.getString(), Locale.MONGO_PASSWORD.getString(), Locale.MONGO_DATABASE.getString())
                : new MongoCredentials(Locale.MONGO_HOST.getString(), Locale.MONGO_PORT.getInt(), Locale.MONGO_DATABASE.getString());
        RedisCredentials redisCreds = new RedisCredentials(Locale.REDIS_HOST.getString(), Locale.REDIS_PORT.getInt(), Locale.REDIS_AUTH.getBoolean(), Locale.REDIS_PASSWORD.getString());
        this.api = API.create(mongoCreds, redisCreds);
        this.thisServer = new Server(Locale.SERVER_NAME.getString(), Locale.SERVER_ID.getString());
    }

    private void setupTasks() {
        this.serverTask = new ServerTask(this);
        this.serverTask.runTaskTimerAsynchronously(this, 20L, 60L);
    }

    private void setupCommands() {
        this.commandManager = new Sunset(this);
        this.commandManager.setPermissionMessage(Locale.NO_PERMISSION.getString());
        this.commandManager.registerType(new RankParameterType(), IRank.class);
        this.commandManager.registerType(new ProfileParameterType(), Profile.class);
        Arrays.asList(
                new DebugCommand(), new RankCommand()
        ).forEach(commandManager::registerCommandWithSubCommands);
    }

    private void setupNms() {
        if (this.getServer().getVersion().contains("1.7")) {
            this.nms = new NMS_v1_7();
            Logger.log(ChatColor.GREEN + "FOUND COMPATIBLE SPIGOT VERSION, IT IS RECOMMENDED TO CHANGE TO 1.8.8, LOADING PLUGIN");
        }
        else if (this.getServer().getVersion().contains("1.8")) {
            this.nms = new NMS_v1_8();
            Logger.log(ChatColor.GREEN + "FOUND FULLY COMPATIBLE SPIGOT VERSION, LOADING PLUGIN");
        } else {
            Logger.error(ChatColor.RED + "FOUND INCOMPATIBLE/UNKNOWN VERSION, DISABLING");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void setupExecutors() {
        this.executor = ForkJoinPool.commonPool();
        this.scheduledExecutor = Executors.newScheduledThreadPool(2);
    }


    public void setupConfigFiles() {
        Locale.init(this);
        Perms.init(this);
        Logger.init();
        CC.setupColors();
    }

    private void setupManagers() {
        this.uuidCache = new UUIDCache();
        this.menuAPI = new MenuAPI(this);
        this.chatManager = new ChatManager(this);
    }

    private void setupListeners() {
        addListener(new PlayerListener());
        addListener(this);
        Arrays.asList(
                new BroadcastSubscriber()
        ).forEach(sub -> api.getMidnight().registerListener(new BroadcastSubscriber()));
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void setupSoftDependencies() {

    }

    private void setupOthers() {
        Tasks.runAsyncLater(() -> joinable = true, 5 * 20L);
    }

    private void addListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }


    private void logInformation(final long milli) {
        Logger.log(ChatColor.GREEN + "Initialized Holiday in " + (System.currentTimeMillis() - milli) + "ms (" + TimeUtil.getDuration(System.currentTimeMillis() - milli) + ").");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void startupCheck(PlayerLoginEvent event) {
        if (!this.joinable) {
            event.setKickMessage(CC.translate("&cServer is still starting up."));
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }

    @Override
    public void onDisable() {
        this.serverTask.cancel();
        this.scheduledExecutor.shutdownNow();
    }

    public ChatColor getRankColor(IRank rank) {
        return ChatColor.valueOf(rank.getColor().toUpperCase());
    }

    public String getNameWithColor(Profile profile) {
        IRank rank = profile.getHighestVisibleRank();
        return (rank.isBold() ? CC.BOLD : "") + (rank.isItalic() ? CC.ITALIC : "") + getRankColor(rank) + profile.getName();
    }

    public String getDisplayNameWithColor(Profile profile) {
        IRank rank = profile.getDisplayRank();
        return (rank.isBold() ? CC.BOLD : "") + (rank.isItalic() ? CC.ITALIC : "") + getRankColor(rank) + profile.getName();
    }
}