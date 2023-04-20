package me.andyreckt.holiday.bukkit;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
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
import me.andyreckt.holiday.bukkit.server.listeners.VisibilityListener;
import me.andyreckt.holiday.bukkit.server.nms.INMS;
import me.andyreckt.holiday.bukkit.server.nms.impl.NMS_v1_7_R4;
import me.andyreckt.holiday.bukkit.server.nms.impl.NMS_v1_8_R3;
import me.andyreckt.holiday.bukkit.server.placeholder.PlaceholderAPIExpansion;
import me.andyreckt.holiday.bukkit.server.tasks.RebootTask;
import me.andyreckt.holiday.bukkit.server.tasks.ServerTask;
import me.andyreckt.holiday.bukkit.user.disguise.DisguiseManager;
import me.andyreckt.holiday.bukkit.user.permission.PermissionManager;
import me.andyreckt.holiday.bukkit.user.visibility.VisibilityHandler;
import me.andyreckt.holiday.bukkit.user.visibility.impl.DefaultVisibilityHandler;
import me.andyreckt.holiday.bukkit.util.Logger;
import me.andyreckt.holiday.bukkit.util.files.ConfigFile;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.menu.MenuAPI;
import me.andyreckt.holiday.bukkit.util.other.Tasks;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.text.StringUtils;
import me.andyreckt.holiday.bukkit.util.uuid.UUIDCache;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.server.Server;
import me.andyreckt.holiday.core.util.duration.Duration;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.http.Skin;
import me.andyreckt.holiday.core.util.http.UUIDFetcher;
import me.andyreckt.holiday.core.util.mongo.MongoCredentials;
import me.andyreckt.holiday.core.util.redis.RedisCredentials;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static me.andyreckt.holiday.bukkit.util.Logger.error;
import static me.andyreckt.holiday.bukkit.util.Logger.log;

@Getter
public final class Holiday extends JavaPlugin {

    @Getter
    private static Holiday instance;

    private API api;

    private INMS nms;
    private PaperCommandManager commandManager;
    private MenuAPI menuAPI;
    private ChatManager chatManager;
    private DisguiseManager disguiseManager;
    private PermissionManager permissionManager;
    @Setter
    private VisibilityHandler visibilityHandler;

    private UUIDCache uuidCache;

    private Executor executor;
    private ScheduledExecutorService scheduledExecutor;

    @Setter
    private boolean joinable = false;

    private Server thisServer;

    private ServerTask serverTask;
    @Setter
    private RebootTask rebootTask;
    private Random random;


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
            this.setupVisibility();
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
        MongoCredentials mongoCreds = Locale.MONGO_AUTH.getBoolean() ?
                new MongoCredentials(Locale.MONGO_HOST.getString(),
                                     Locale.MONGO_PORT.getInt(),
                                     Locale.MONGO_USERNAME.getString(),
                                     Locale.MONGO_PASSWORD.getString(),
                                     Locale.MONGO_DATABASE.getString()) :
                new MongoCredentials(Locale.MONGO_HOST.getString(),
                                     Locale.MONGO_PORT.getInt(),
                                     Locale.MONGO_DATABASE.getString());
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
        this.commandManager = new PaperCommandManager(this);

        this.commandManager.enableUnstableAPI("help");

        this.commandManager.getCommandContexts().registerContext(Duration.class, c -> Duration.of(c.popFirstArg()));
        this.commandManager.getCommandContexts().registerContext(Player.class, c -> {
            String source = c.popFirstArg();
            if (c.getIssuer().isPlayer() && (source.equalsIgnoreCase("self") || source.equals(""))) {
                return c.getPlayer();
            }
            if ((!c.getIssuer().isPlayer()) && (source.equalsIgnoreCase("self") || source.equals(""))) {
                throw new InvalidCommandArgument(ChatColor.RED + "Are you insane?");
            }

            Player player = Bukkit.getPlayer(source);

            if (player == null) {
                throw new InvalidCommandArgument(Locale.PLAYER_NOT_FOUND.getString());
            }

            return (player);
        });
        this.commandManager.getCommandContexts().registerContext(UUID.class, c -> {
            String source = c.popFirstArg();
            if (c.getIssuer().isPlayer() && (source.equalsIgnoreCase("self") || source.equals(""))) {
                return c.getPlayer().getUniqueId();
            }
            try {
                return UUID.fromString(source);
            } catch (Exception ignored) {
                if (this.getDisguiseManager().isDisguised(source)) {
                    return this.getDisguiseManager().getDisguise(source).getUuid();
                }

                UUID uuid = this.getUuidCache().uuid(source);

                if (uuid != null) {
                    return uuid;
                }

                if (!Locale.SERVER_CREATE_PROFILE_IF_NOT_EXISTS.getBoolean()) {
                    throw new InvalidCommandArgument(Locale.PLAYER_NOT_FOUND.getString());
                }

                UUID fetchedUUID = UUIDFetcher.getSync(source);

                if (fetchedUUID == null) {
                    throw new InvalidCommandArgument(Locale.PLAYER_NOT_FOUND.getString());
                }

                return fetchedUUID;
            }
        });
        this.commandManager.getCommandContexts().registerContext(IRank.class, c -> {
            String source = c.popFirstArg();

            if (source == null) {
                return Holiday.getInstance().getApi().getDefaultRank();
            }

            if (c.getIssuer().isPlayer() && source.equals("")) {
                return Holiday.getInstance().getApi().getProfile(c.getPlayer().getUniqueId()).getHighestRank();
            }
            if (source.equalsIgnoreCase("default")) return Holiday.getInstance().getApi().getDefaultRank();

            IRank rank = Holiday.getInstance().getApi().getRank(source);

            if (rank == null) {
                throw new InvalidCommandArgument(Locale.RANK_NOT_FOUND.getString());
            }

            return (rank);
        });
        this.commandManager.getCommandContexts().registerContext(Profile.class, c -> {
            final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
            final String UUID_REGEX_NO_HYPHENS = "[0-9a-fA-F]{32}";

            String source = c.popFirstArg();

            if (source.equals("")) {
                throw new InvalidCommandArgument(Locale.NEED_NAME.getString());
            }

            Holiday plugin = Holiday.getInstance();

            if (source.matches(UUID_REGEX) || source.matches(UUID_REGEX_NO_HYPHENS)) {
                UUID uuid = UUID.fromString(source);
                return ((HolidayAPI) plugin.getApi()).getUserManager().getProfileNoCreate(uuid);
            }

            if (c.getIssuer().isPlayer() && (source.equalsIgnoreCase("self"))) {
                return plugin.getApi().getProfile(c.getPlayer().getUniqueId());
            }

            if (Bukkit.getPlayer(source) != null) {
                return plugin.getApi().getProfile(Bukkit.getPlayer(source).getUniqueId());
            }

            UUID cachedUUID = null;

            if (plugin.getDisguiseManager().isDisguised(source)) {
                cachedUUID = plugin.getDisguiseManager().getDisguise(source).getUuid();
            }

            if (cachedUUID == null && plugin.getUuidCache().uuid(source.toLowerCase()) == null) {
                if (!Locale.SERVER_CREATE_PROFILE_IF_NOT_EXISTS.getBoolean()) {
                    throw new InvalidCommandArgument(Locale.PLAYER_NOT_FOUND.getString());
                }

                UUID fetchedUUID = UUIDFetcher.getSync(source);

                if (fetchedUUID == null) {
                    throw new InvalidCommandArgument(Locale.PLAYER_NOT_FOUND.getString());
                }

                Profile profile = plugin.getApi().getProfile(fetchedUUID);
                profile.setName(source.toLowerCase());
                plugin.getApi().saveProfile(profile);
                return plugin.getApi().getProfile(fetchedUUID);
            }

            cachedUUID = plugin.getUuidCache().uuid(source.toLowerCase());

            return plugin.getApi().getProfile(cachedUUID);
        });

        this.commandManager.getCommandCompletions().registerCompletion("ranks", c -> api.getRanks().stream().map(IRank::getName).collect(Collectors.toList()));
        this.commandManager.getCommandCompletions().registerCompletion("servers", c -> api.getServers().keySet());
        this.commandManager.getCommandCompletions().registerCompletion("dnames", c -> disguiseManager.getUnusedNames());
        this.commandManager.getCommandCompletions().registerCompletion("dskins", c -> Skin.SKINS.keySet());
        this.commandManager.getCommandCompletions().registerCompletion("nothing", c -> Collections.emptyList());
        this.commandManager.getCommandCompletions().registerCompletion("materials", c -> Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()));
        this.commandManager.getCommandCompletions().registerCompletion("enchantements", c -> Arrays.stream(Enchantment.values()).map(Enchantment::getName).collect(Collectors.toList()));

        this.commandManager.getCommandConditions().addCondition("dev", c -> {
            if (!c.getIssuer().isPlayer()) {
                return;
            }

            if (!Logger.DEV) {
                throw new ConditionFailedException("&cThis command is not available in production");
            }
        });
        this.commandManager.getCommandConditions().addCondition("player", c -> {
            if (!c.getIssuer().isPlayer()) {
                throw new ConditionFailedException("&cThis command is only available for players");
            }
        });
        this.commandManager.getCommandConditions().addCondition("console", c -> {
            if (c.getIssuer().isPlayer()) {
                throw new ConditionFailedException("&cThis command is only available for console");
            }
        });

        Arrays.asList(
                new ChatCommand(), new ConversationCommands(), new DebugCommand(),
                new DisguiseCommands(), new EssentialCommands(), new GamemodeCommands(),
                new GrantCommands(), new PunishmentCommands(), new PunishmentRemoveCommands(),
                new RankCommand(), new ServerManagerCommand(), new SettingsCommands(),
                new ShutdownCommands(), new SocialCommands(), new StaffCommands(),
                new TeleportCommands(), new UserCommand(), new WhitelistCommand()
        ).forEach(commandManager::registerCommand);
    }

    private void setupNms() {
        if (this.getServer().getVersion().contains("1.7")) {
            this.nms = new NMS_v1_7_R4();
            log(ChatColor.GREEN + "FOUND COMPATIBLE SPIGOT VERSION, IT IS RECOMMENDED TO CHANGE TO 1.8.8, LOADING PLUGIN");
        } else if (this.getServer().getVersion().contains("1.8")) {
            this.nms = new NMS_v1_8_R3();
            log(ChatColor.GREEN + "FOUND FULLY COMPATIBLE SPIGOT VERSION, LOADING PLUGIN");
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
        this.random = new Random();
        this.uuidCache = new UUIDCache(this);
        this.menuAPI = new MenuAPI(this);
        this.chatManager = new ChatManager(this);
        this.disguiseManager = new DisguiseManager(this, this.nms);
        this.permissionManager = new PermissionManager();
    }

    private void setupVisibility() {
        if (Locale.USE_VISIBILITY_HANDLER.getBoolean()) {
            this.visibilityHandler = new DefaultVisibilityHandler(this);
            this.addListener(new VisibilityListener());

            this.commandManager.getCommandCompletions().registerCompletion("players", c -> {
                if (c.getIssuer().isPlayer()) {
                    return this.visibilityHandler.getAllOnlineTo(c.getPlayer()).stream().map(Player::getName).collect(Collectors.toList());
                }

                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            });
        }
    }

    private void setupListeners() {
        Arrays.asList(
                new PlayerListener(), new ChatListener()
        ).forEach(this::addListener);

        HolidayAPI _api = (HolidayAPI) api;

        _api.setBroadcastConsumer(packet -> {
            if (packet.getPermission() != null) {
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
                log(CC.translate(packet.getMessage()));
            } else {
                Bukkit.broadcastMessage(CC.translate(packet.getMessage()));
            }
        });

        _api.setServerUpdateConsumer(server -> {
            if (server.getServerId().equals(thisServer.getServerId())) {
                this.thisServer = server;
                StringUtils.setSlots(this.thisServer.getMaxPlayers());
            }
        });

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void setupSoftDependencies() {
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
        Duration duration = Duration.of(System.currentTimeMillis() - milli);
        log(" ");
        log(CC.CHAT + "-----------------------------------------------");
        log(CC.PRIMARY + "Plugin: " + CC.SECONDARY + "Holiday");
        log(CC.PRIMARY + "Version: " + CC.SECONDARY + getDescription().getVersion());
        log(CC.PRIMARY + "Author: " + CC.SECONDARY + getDescription().getAuthors().get(0));
        log(CC.PRIMARY + "Startup time: " + CC.SECONDARY + duration.get() + "ms " + CC.PRIMARY + "(" + duration.toSmallRoundedTime() + ")");
        log(CC.CHAT + "-----------------------------------------------");
        log(" ");
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
        } catch (IOException ignored) {
        }

        return "127.0.0.1";
    }

}