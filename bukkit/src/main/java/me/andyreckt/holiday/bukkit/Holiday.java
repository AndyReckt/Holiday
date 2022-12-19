package me.andyreckt.holiday.bukkit;

import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.server.IServer;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.commands.*;
import me.andyreckt.holiday.bukkit.server.chat.ChatManager;
import me.andyreckt.holiday.bukkit.server.listeners.ChatListener;
import me.andyreckt.holiday.bukkit.server.listeners.PlayerListener;
import me.andyreckt.holiday.bukkit.server.nms.INMS;
import me.andyreckt.holiday.bukkit.server.nms.impl.NMS_v1_7;
import me.andyreckt.holiday.bukkit.server.nms.impl.NMS_v1_8;
import me.andyreckt.holiday.bukkit.server.redis.packet.*;
import me.andyreckt.holiday.bukkit.server.redis.subscriber.*;
import me.andyreckt.holiday.bukkit.server.tasks.RebootTask;
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
import me.andyreckt.holiday.bukkit.util.text.StringUtils;
import me.andyreckt.holiday.bukkit.util.uuid.UUIDCache;
import me.andyreckt.holiday.core.server.Server;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import me.andyreckt.holiday.core.util.mongo.MongoCredentials;
import me.andyreckt.holiday.core.util.redis.RedisCredentials;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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
    @Setter private RebootTask rebootTask;

    private boolean protocolEnabled = false;


    @Override
    public void onEnable() {
        instance = this;
        long time = System.currentTimeMillis();

        try {
            this.setupNms();
            this.setupConfigFiles();
            this.setupApi();
            this.setupServer();

            this.setupExecutors();
            this.setupManagers();
            this.setupTasks();
            this.setupListeners();
            this.setupCommands();
            this.setupSoftDependencies();
            this.finishSetup();

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
    }

    private void setupServer() {
        this.thisServer = new Server(Locale.SERVER_NAME.getString(), Locale.SERVER_ID.getString(), getIP(), getServer().getPort());
        IServer server = this.getApi().getServer(thisServer.getServerId());
        if (server == null) return;
        this.thisServer.setChatMuted(server.isChatMuted());
        this.thisServer.setChatDelay(server.getChatDelay());
        this.thisServer.setWhitelisted(server.isWhitelisted());
        this.thisServer.setWhitelistRank(server.getWhitelistRank());
        this.thisServer.setWhitelistedPlayers(server.getWhitelistedPlayers());
        this.thisServer.keepAlive();
        StringUtils.setSlots(this.thisServer.getMaxPlayers());
    }

    private void setupTasks() {
        this.serverTask = new ServerTask(this);
        this.serverTask.runTaskTimerAsynchronously(this, 20L, 4 * 20L);
    }

    private void setupCommands() {
        this.commandManager = new Sunset(this);
        this.commandManager.setPermissionMessage(Locale.NO_PERMISSION.getString());
        this.commandManager.registerType(new RankParameterType(), IRank.class);
        this.commandManager.registerType(new ProfileParameterType(), Profile.class);

        Arrays.asList(
                new DebugCommand(), new RankCommand(), new ChatCommand(),
                new WhitelistCommand(), new ServerManagerCommand(),
                new GamemodeCommands()
        ).forEach(commandManager::registerCommandWithSubCommands);
        Arrays.asList(
                new ChatCommand(), new ServerManagerCommand(), new GamemodeCommands(),
                new TeleportCommands(), new SocialCommands(), new SettingsCommands(),
                new ConversationCommands(), new GrantCommands(), new ShutdownCommands(),
                new GeneralCommands(), new OtherCommands()
        ).forEach(commandManager::registerCommands);
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
        this.uuidCache = new UUIDCache(this);
        this.menuAPI = new MenuAPI(this);
        this.chatManager = new ChatManager(this);
    }

    private void setupListeners() {
        Arrays.asList(
                new PlayerListener(), new ChatListener(), this
        ).forEach(this::addListener);

        api.getRedis().registerAdapter(CrossServerCommandPacket.class, new ServerSubscriber());
        api.getRedis().registerAdapter(BroadcastPacket.class, new BroadcastSubscriber());
        api.getRedis().registerAdapter(MessagePacket.class, new MessageSubscriber());
        api.getRedis().registerAdapter(PlayerMessagePacket.class, new PlayerMessageSubscriber());
        api.getRedis().registerAdapter(ReportPacket.class, new ReportSubscriber());
        api.getRedis().registerAdapter(HelpopPacket.class, new HelpopSubscriber());

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void setupSoftDependencies() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            this.protocolEnabled = true;
        }
    }

    private void finishSetup() {
        Tasks.runAsyncLater(() -> {
            joinable = true;
            String str = Locale.SERVER_STARTUP.getString()
                    .replace("%server%", thisServer.getServerName());
            api.getRedis().sendPacket(new BroadcastPacket(str, Perms.ADMIN_VIEW_NOTIFICATIONS.get()));
        }, 5 * 20L);
    }

    private void addListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }


    private void logInformation(final long milli) {
        long duration = System.currentTimeMillis() - milli;
        Logger.log(" ");
        Logger.log(CC.CHAT + "-----------------------------------------------");
        Logger.log(CC.PRIMARY + "Plugin: " + CC.SECONDARY + "Holiday");
        Logger.log(CC.PRIMARY + "Version: " + CC.SECONDARY + getDescription().getVersion());
        Logger.log(CC.PRIMARY + "Author: " + CC.SECONDARY + getDescription().getAuthors().get(0));
        Logger.log(CC.PRIMARY + "Startup time: " + CC.SECONDARY + duration + "ms " + CC.PRIMARY + "(" + TimeUtil.getDuration(duration) + ")");
        Logger.log(CC.CHAT + "-----------------------------------------------");
        Logger.log(" ");
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
        String str = Locale.SERVER_SHUTDOWN.getString()
                .replace("%server%", thisServer.getServerName());
        api.getRedis().sendPacket(new BroadcastPacket(str, Perms.ADMIN_VIEW_NOTIFICATIONS.get()));
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

    public String getDisplayNameWithColorAndVanish(Profile profile) {
        return (profile.getStaffSettings().isVanished() ? CC.GRAY + "*" : "") + getDisplayNameWithColor(profile);
    }

    public static String getIP() {
        if (Locale.USE_CUSTOM_IP.getBoolean()) {
            return Locale.CUSTOM_IP.getString();
        }

        String urlString = "https://checkip.amazonaws.com/";
        try {
            URL url = new URL(urlString);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
                return br.readLine();
            }
        } catch (IOException ignored) {}

        return "127.0.0.1";
    }

}