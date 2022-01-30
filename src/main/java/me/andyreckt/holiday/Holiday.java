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
import me.andyreckt.holiday.listeners.ProfileListener;
import me.andyreckt.holiday.listeners.PunishmentsListener;
import me.andyreckt.holiday.other.enums.StaffMessageType;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.player.disguise.DisguiseHandler;
import me.andyreckt.holiday.player.grant.GrantHandler;
import me.andyreckt.holiday.player.punishments.PunishmentHandler;
import me.andyreckt.holiday.player.rank.RankHandler;
import me.andyreckt.holiday.player.staff.StaffHandler;
import me.andyreckt.holiday.player.staff.StaffListeners;
import me.andyreckt.holiday.server.ServerHandler;
import me.andyreckt.holiday.server.chat.ChatHandler;
import me.andyreckt.holiday.server.reboot.RebootTask;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.StringUtil;
import me.andyreckt.holiday.utils.Tasks;
import me.andyreckt.holiday.utils.command.CommandHandler;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import me.andyreckt.holiday.utils.packets.Pidgin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.*;


@Getter
public final class Holiday extends JavaPlugin {

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
    private DisguiseHandler disguiseHandler;
    private RankHandler rankHandler;
    private GrantHandler grantHandler;
    private ServerHandler serverHandler;
    private ChatHandler chatHandler;
    private StaffHandler staffHandler;
    private MenuAPI menuAPI;

    private Executor dbExecutor, executor;
    private ScheduledExecutorService scheduledExecutor;

    @Setter
    private RebootTask rebootTask;


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
        this.gson = new GsonBuilder().serializeNulls().create();
        setupConfigFiles();
        setupExecutors();
        setupDatabases();
        setupHandlers();
        setupListeners();
        setupRunnables();
        setupCommands();
        setupSoftDependencies();
        setupOthers();


        infoConsole(ChatColor.GREEN + "Successfully Loaded!");
        sendServerStartup();
    }

    private void setupOthers() {
        DisguiseCommands.setup(settings);
    }

    @Override
    public void onDisable() {
        this.serverHandler.stop();
        this.scheduledExecutor.shutdownNow();
    }

    private void setupCommands() {
        this.commandHandler = new CommandHandler(this);
        this.commandHandler.hook();
        CommandHandler.loadCommandsFromPackage(this, "me.andyreckt.holiday.commands");
    }

    private void sendServerStartup() {
        Tasks.runAsyncLater(() -> redis.sendPacket(new StaffMessages.StaffMessagesPacket(StringUtil.addNetworkPlaceholder(
                        messages.getString("SERVER.STARTUP")
                                .replace("<server>", settings.getString("SERVER.NICENAME"))),
                        StaffMessageType.ADMIN)),
                20 * 5L);
    }

    private void setupExecutors() {
        this.dbExecutor = Executors.newFixedThreadPool(1);
        this.executor = ForkJoinPool.commonPool();
        this.scheduledExecutor = Executors.newScheduledThreadPool(2);
    }


    private void setupConfigFiles() {
        this.settings = new BasicConfigurationFile(this, "settings");
        this.messages = new BasicConfigurationFile(this, "messages");
    }

    private void setupDatabases() {
        this.mongoDatabase = new MongoDB(settings).getDatabase();
        this.redisPool = new Redis(settings);
        this.jedisPool = redisPool.getJedis();
        this.redis = redisPool.getPidgin();
    }

    private void setupHandlers() {
        this.punishmentHandler = new PunishmentHandler();
        this.profileHandler = new ProfileHandler();
        this.rankHandler = new RankHandler();
        this.disguiseHandler = new DisguiseHandler(this);
        this.serverHandler = new ServerHandler(this);
        this.grantHandler = new GrantHandler();
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
        this.addListener(new ProfileListener());
        this.addListener(new ChatListener());
        this.addListener(new PunishmentsListener());
        new StaffListeners(this);
    }

    private void setupSoftDependencies() {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("LunarClient-API") && settings.getBoolean("LUNAR.ENABLED"))
            lunarEnabled = true;
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) protocolEnabled = true;
    }


    private void addListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public void infoConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(CC.translate(message));
    }
}
