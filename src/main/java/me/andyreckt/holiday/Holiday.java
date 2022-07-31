package me.andyreckt.holiday;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoDatabase;
import io.github.zowpy.menu.MenuAPI;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.commands.DisguiseCommands;
import me.andyreckt.holiday.database.mongo.MongoDB;
import me.andyreckt.holiday.database.redis.Redis;
import me.andyreckt.holiday.database.redis.packet.StaffMessages;
import me.andyreckt.holiday.listeners.ChatListener;
import me.andyreckt.holiday.listeners.OtherListeners;
import me.andyreckt.holiday.listeners.ProfileListener;
import me.andyreckt.holiday.listeners.PunishmentsListener;
import me.andyreckt.holiday.other.LunarNametagsTask;
import me.andyreckt.holiday.other.enums.StaffMessageType;
import me.andyreckt.holiday.other.placeholder.PlaceholderAPIExpansion;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.player.disguise.IDisguiseHandler;
import me.andyreckt.holiday.player.disguise.impl.v1_7.DisguiseHandler_1_7;
import me.andyreckt.holiday.player.disguise.impl.v1_8.DisguiseHandler_1_8;
import me.andyreckt.holiday.player.grant.GrantHandler;
import me.andyreckt.holiday.player.punishments.PunishmentHandler;
import me.andyreckt.holiday.player.rank.RankHandler;
import me.andyreckt.holiday.player.staff.StaffHandler;
import me.andyreckt.holiday.player.staff.StaffListeners;
import me.andyreckt.holiday.server.ServerHandler;
import me.andyreckt.holiday.server.chat.ChatHandler;
import me.andyreckt.holiday.server.nms.INMS;
import me.andyreckt.holiday.server.nms.impl.NMS_v1_7;
import me.andyreckt.holiday.server.nms.impl.NMS_v1_8;
import me.andyreckt.holiday.server.reboot.RebootTask;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.StringUtil;
import me.andyreckt.holiday.utils.Tasks;
import me.andyreckt.holiday.utils.command.CommandHandler;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import me.andyreckt.holiday.utils.packets.Pidgin;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.*;


@Getter
public final class Holiday extends JavaPlugin implements Listener{

    @Getter
    private static Holiday instance;

    private boolean lunarEnabled = false, protocolEnabled = false;
    private Gson gson;

    private BasicConfigurationFile settings, messages;

    private Redis redisPool;
    private Pidgin redis;
    private JedisPool jedisPool;

    private MongoDatabase mongoDatabase;

    private CommandHandler commandHandler;
    private PunishmentHandler punishmentHandler;
    private ProfileHandler profileHandler;
    private IDisguiseHandler disguiseHandler;
    private RankHandler rankHandler;
    private GrantHandler grantHandler;
    private ServerHandler serverHandler;
    private ChatHandler chatHandler;
    private StaffHandler staffHandler;
    private MenuAPI menuAPI;
    private INMS nmsHandler;

    private Executor dbExecutor, executor;
    private ScheduledExecutorService scheduledExecutor;

    @Setter
    private RebootTask rebootTask;
    private boolean joinable = false;


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

    private void loadPlugin() {
        instance = this;
        long time = System.currentTimeMillis();
        this.gson = new GsonBuilder().serializeNulls().create();
        setupConfigFiles();
        setupExecutors();
        setupDatabases();
        setupNms();
        setupHandlers();
        setupListeners();
        setupRunnables();
        setupCommands();
        setupSoftDependencies();
        setupOthers();

        logInformation(time);
        sendServerStartup();
    }




    @Override
    public void onDisable() {
        this.serverHandler.stop();
        this.scheduledExecutor.shutdownNow();
        this.profileHandler.cachedProfiles().forEach(Profile::saveOnStop);
    }

    private void setupCommands() {
        this.commandHandler = new CommandHandler(this);
        this.commandHandler.hook();
        this.commandHandler.loadCommandsFromPackage(this, "me.andyreckt.holiday.commands");
        this.commandHandler.loadCommandsFromPackage(this, "me.andyreckt.holiday.commands.staff");
    }

    private void setupNms() {
        if (this.getServer().getVersion().contains("1.7")) {
            this.nmsHandler = new NMS_v1_7();
            infoConsole(ChatColor.GOLD + "FOUND NOT FULLY COMPATIBLE SPIGOT VERSION, IT IS RECOMMENDED TO CHANGE TO 1.8.8");
        }
        else if (this.getServer().getVersion().contains("1.8")) {
            this.nmsHandler = new NMS_v1_8();
            infoConsole(ChatColor.GREEN + "FOUND COMPATIBLE SPIGOT VERSION, LOADING PLUGIN");
        } else {
            infoConsole(ChatColor.RED + "FOUND IMCOMPATIBLE/UNKNOWN VERSION, DISABLING");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void sendServerStartup() {
        Tasks.runAsyncLater(() -> {
                    this.joinable = true;
                    redis.sendPacket(new StaffMessages.StaffMessagesPacket(StringUtil.addNetworkPlaceholder(
                            messages.getString("SERVER.STARTUP")
                                    .replace("<server>", settings.getString("SERVER.NICENAME"))),
                            StaffMessageType.ADMIN));
                },
                20 * 5L);
    }

    private void setupExecutors() {
        this.dbExecutor = Executors.newFixedThreadPool(1);
        this.executor = ForkJoinPool.commonPool();
        this.scheduledExecutor = Executors.newScheduledThreadPool(2);
    }


    public void setupConfigFiles() {
        this.settings = new BasicConfigurationFile(this, "settings");
        this.messages = new BasicConfigurationFile(this, "messages");
        CC.setupColors(settings);
    }

    private void setupDatabases() {
        this.mongoDatabase = new MongoDB(settings).getDatabase();
        this.redisPool = new Redis(settings);
        this.jedisPool = redisPool.getJedis();
        this.redis = redisPool.getPidgin();
    }

    private void setupHandlers() {
        this.rankHandler = new RankHandler();
        this.grantHandler = new GrantHandler();
        this.disguiseHandler = this.nmsHandler instanceof NMS_v1_8 ? new DisguiseHandler_1_8(this) : new DisguiseHandler_1_7(this);
        this.profileHandler = new ProfileHandler();
        this.serverHandler = new ServerHandler(this);
        this.punishmentHandler = new PunishmentHandler();
        this.chatHandler = new ChatHandler(this.settings, serverHandler.getThisServer());
        this.staffHandler = new StaffHandler(this);
        this.menuAPI = new MenuAPI(this);
    }


    private void setupRunnables() {
        Runnable refreshGrants = () -> this.grantHandler.refreshGrants();
        Runnable refreshServer = () -> this.serverHandler.save();

        this.scheduledExecutor.scheduleAtFixedRate(refreshGrants, 0, 1, TimeUnit.MINUTES);
        this.scheduledExecutor.scheduleAtFixedRate(refreshServer, 25, 25, TimeUnit.SECONDS);
    }

    private void setupListeners() {
        this.addListener(this);
        this.addListener(new ProfileListener());
        this.addListener(new ChatListener());
        this.addListener(new PunishmentsListener());
        this.addListener(new OtherListeners());
        new StaffListeners(this);
    }

    private void setupSoftDependencies() {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("LunarClient-API") && settings.getBoolean("LUNAR.ENABLED"))
            lunarEnabled = true;
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) protocolEnabled = true;
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) new PlaceholderAPIExpansion(this).register();

        if (lunarEnabled) new LunarNametagsTask(this);
    }

    private void setupOthers() {
        DisguiseCommands.setup(settings);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Metrics metrics = new Metrics(this, 15604);
        metrics.addCustomChart(new SimplePie("lunar_enabled", () -> lunarEnabled ? "Yes" : "No"));
    }

    private void addListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public void infoConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(CC.translate(message));
    }

    private void logInformation(final long milli) {
        infoConsole(ChatColor.GREEN + "Initialized Holiday in " + (System.currentTimeMillis() - milli) + "ms (" + DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - milli, true, true) + ").");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void startupCheck(PlayerLoginEvent event) {
        if (!this.joinable) {
            event.setKickMessage(CC.translate("&cServer is still starting up."));
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }
}
