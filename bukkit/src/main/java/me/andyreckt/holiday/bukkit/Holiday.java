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
import me.andyreckt.holiday.bukkit.server.nms.impl.NMS_v1_7_R4;
import me.andyreckt.holiday.bukkit.server.nms.impl.NMS_v1_8_R3;
import me.andyreckt.holiday.bukkit.server.placeholder.PlaceholderAPIExpansion;
import me.andyreckt.holiday.bukkit.server.tasks.RebootTask;
import me.andyreckt.holiday.bukkit.server.tasks.ServerTask;
import me.andyreckt.holiday.bukkit.user.disguise.DisguiseManager;
import me.andyreckt.holiday.bukkit.user.permission.PermissionManager;
import me.andyreckt.holiday.bukkit.util.Logger;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.menu.MenuAPI;
import me.andyreckt.holiday.bukkit.util.other.Tasks;
import me.andyreckt.holiday.bukkit.util.sunset.Sunset;
import me.andyreckt.holiday.bukkit.util.sunset.parameter.custom.ProfileParameterType;
import me.andyreckt.holiday.bukkit.util.sunset.parameter.custom.RankParameterType;
import me.andyreckt.holiday.bukkit.util.sunset.parameter.custom.UUIDParameterType;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.text.StringUtils;
import me.andyreckt.holiday.bukkit.util.uuid.UUIDCache;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.server.Server;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import me.andyreckt.holiday.core.util.mongo.MongoCredentials;
import me.andyreckt.holiday.core.util.redis.RedisCredentials;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;

@Getter
public final class Holiday extends JavaPlugin {

    @Getter
    private static Holiday instance;

    private API api;

    private INMS nms;
    private Sunset commandManager;
    private MenuAPI menuAPI;
    private ChatManager chatManager;
    private DisguiseManager disguiseManager;
    private PermissionManager permissionManager;

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
        if (Locale.MONGO_URI_MODE.getBoolean()) {
            mongoCreds = new MongoCredentials(Locale.MONGO_URI.getString(), Locale.MONGO_DATABASE.getString());
        }
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
        this.commandManager.registerType(new UUIDParameterType(), UUID.class);
        Arrays.asList(
                new DebugCommand(), new RankCommand(), new ChatCommand(),
                new WhitelistCommand(), new ServerManagerCommand(),
                new GamemodeCommands(), new UserCommand()
        ).forEach(commandManager::registerCommandWithSubCommands);
        Arrays.asList(
                new ChatCommand(), new ServerManagerCommand(), new GamemodeCommands(),
                new TeleportCommands(), new SocialCommands(), new SettingsCommands(),
                new ConversationCommands(), new GrantCommands(), new ShutdownCommands(),
                new EssentialCommands(), new StaffCommands(), new DisguiseCommands(),
                new PunishmentCommands(), new PunishmentRemoveCommands()
        ).forEach(commandManager::registerCommands);
    }

    private void setupNms() {
        if (this.getServer().getVersion().contains("1.7")) {
            this.nms = new NMS_v1_7_R4();
            Logger.log(ChatColor.GREEN + "FOUND COMPATIBLE SPIGOT VERSION, IT IS RECOMMENDED TO CHANGE TO 1.8.8, LOADING PLUGIN");
        }
        else if (this.getServer().getVersion().contains("1.8")) {
            this.nms = new NMS_v1_8_R3();
            Logger.log(ChatColor.GREEN + "FOUND FULLY COMPATIBLE SPIGOT VERSION, LOADING PLUGIN");
        } else {
            Logger.error(ChatColor.RED + "FOUND INCOMPATIBLE/UNKNOWN VERSION, DISABLING");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void setupExecutors() {
        this.executor = Executors.newFixedThreadPool(2);
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
        this.disguiseManager = new DisguiseManager(this, this.nms);
        this.permissionManager = new PermissionManager();
    }

    private void setupListeners() {
        Arrays.asList(
                new PlayerListener(), new ChatListener()
        ).forEach(this::addListener);

        HolidayAPI _api = (HolidayAPI) api;

        _api.setBroadcastConsumer(packet -> {
            if(packet.getPermission() != null) {
                if (packet.getAlertType() != null) {
                    Bukkit.getOnlinePlayers().stream()
                            .filter(player -> player.hasPermission(packet.getPermission()))
                            .filter(player -> packet.getAlertType().isAlerts(player.getUniqueId()))
                            .forEach(player -> player.sendMessage(CC.translate(packet.getMessage())));
                } else {
                    Bukkit.getOnlinePlayers().stream()
                            .filter(player -> player.hasPermission(packet.getPermission()))
                            .forEach(player -> player.sendMessage(CC.translate(packet.getMessage())));
                }
                Logger.log(CC.translate(packet.getMessage()));
            } else {
                Bukkit.broadcastMessage(CC.translate(packet.getMessage()));
            }
        });

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void setupSoftDependencies() {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            this.protocolEnabled = true;
        }

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIExpansion(this).register();
        }
    }

    private void finishSetup() {
        Tasks.runAsyncLater(() -> {
            joinable = true;
            String str = Locale.SERVER_STARTUP.getString()
                    .replace("%server%", thisServer.getServerName());
            PacketHandler.send(new BroadcastPacket(str, Perms.ADMIN_VIEW_NOTIFICATIONS.get(), AlertType.SERVER));
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

    @Override
    public void onDisable() {
        if (!joinable) return;
        String str = Locale.SERVER_SHUTDOWN.getString()
                .replace("%server%", thisServer.getServerName());
        PacketHandler.send(new BroadcastPacket(str, Perms.ADMIN_VIEW_NOTIFICATIONS.get(), AlertType.SERVER));
        this.serverTask.cancel();
        this.scheduledExecutor.shutdownNow();
    }

    private String getIP() {
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