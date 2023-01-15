package me.andyreckt.holiday.bukkit.server.tasks;

import me.andyreckt.holiday.bukkit.Holiday;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;

public class ServerTask extends BukkitRunnable {

    private final Holiday plugin;

    public ServerTask(Holiday plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        double[] tps = new double[]{Bukkit.spigot().getTPS()[0], Bukkit.spigot().getTPS()[1], Bukkit.spigot().getTPS()[2]};
        this.plugin.getThisServer().setJoinable(this.plugin.isJoinable());
        this.plugin.getThisServer().setTps(tps);
        this.plugin.getThisServer().setOnlinePlayers(this.plugin.getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()));
        this.plugin.getThisServer().setMemoryFree((int) (Runtime.getRuntime().freeMemory() / 1024 / 1024));
        this.plugin.getThisServer().setMemoryMax((int) (Runtime.getRuntime().maxMemory() / 1024 / 1024));
        this.plugin.getThisServer().setLastKeepAlive(System.currentTimeMillis());
        this.plugin.getThisServer().keepAlive();
    }
}
