package me.andyreckt.holiday.bukkit;

import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.util.menu.MenuAPI;
import me.andyreckt.holiday.bukkit.util.sunset.Sunset;
import me.andyreckt.holiday.bukkit.util.sunset.parameter.custom.ProfileParameterType;
import me.andyreckt.holiday.bukkit.util.sunset.parameter.custom.RankParameterType;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.uuid.HolidayUUIDCache;
import me.andyreckt.holiday.bukkit.util.uuid.UUIDCache;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;

@Getter
public final class Holiday extends JavaPlugin implements Listener {

    @Getter
    private static Holiday instance;

    private API api;

    private boolean lunarEnabled = false, protocolEnabled = false;

    private Sunset commandHandler;
    private MenuAPI menuAPI;

    private UUIDCache uuidCache;

    private Executor executor;
    private ScheduledExecutorService scheduledExecutor;

    @Setter
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
        setupConfigFiles();
        this.api = API.create();

        setupExecutors();
        setupNms();
        setupHandlers();
        setupListeners();
        setupCommands();
        setupSoftDependencies();
        setupOthers();

        logInformation(time);
    }




    @Override
    public void onDisable() {
        this.scheduledExecutor.shutdownNow();
    }

    private void setupCommands() {
        this.commandHandler = new Sunset(this);
        this.commandHandler.registerType(new RankParameterType(), IRank.class);
        this.commandHandler.registerType(new ProfileParameterType(), Profile.class);
    }

    private void setupNms() {
        if (this.getServer().getVersion().contains("1.7")) {
            this.nmsHandler = new NMS_v1_7();
            infoConsole(ChatColor.GOLD + "FOUND COMPATIBLE SPIGOT VERSION, IT IS RECOMMENDED TO CHANGE TO 1.8.8, LOADING PLUGIN");
        }
        else if (this.getServer().getVersion().contains("1.8")) {
            this.nmsHandler = new NMS_v1_8();
            infoConsole(ChatColor.GREEN + "FOUND FULLY COMPATIBLE SPIGOT VERSION, LOADING PLUGIN");
        } else {
            infoConsole(ChatColor.RED + "FOUND IMCOMPATIBLE/UNKNOWN VERSION, DISABLING");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void setupExecutors() {
        this.executor = ForkJoinPool.commonPool();
        this.scheduledExecutor = Executors.newScheduledThreadPool(2);
    }


    public void setupConfigFiles() {
        CC.setupColors();
    }

    private void setupHandlers() {
        HolidayUUIDCache.init();
        this.uuidCache = HolidayUUIDCache.getImpl();
        this.menuAPI = new MenuAPI(this);
    }

    private void setupListeners() {

    }

    private void setupSoftDependencies() {

    }

    private void setupOthers() {

    }

    private void addListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public void infoConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(CC.translate(message));
    }

    private void logInformation(final long milli) {
        infoConsole(ChatColor.GREEN + "Initialized Holiday in " + (System.currentTimeMillis() - milli) + "ms (" + TimeUtil.getDuration(System.currentTimeMillis() - milli) + ").");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void startupCheck(PlayerLoginEvent event) {
        if (!this.joinable) {
            event.setKickMessage(CC.translate("&cServer is still starting up."));
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }
}