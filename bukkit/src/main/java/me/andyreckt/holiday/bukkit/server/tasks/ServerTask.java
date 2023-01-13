package me.andyreckt.holiday.bukkit.server.tasks;

import me.andyreckt.holiday.api.server.IServer;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.core.server.Server;
import me.andyreckt.holiday.core.util.json.GsonProvider;
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
        this.plugin.getThisServer().setJoinable(this.plugin.isJoinable());
        this.plugin.getThisServer().setTps(this.plugin.getServer().spigot().getTPS());
        this.plugin.getThisServer().setOnlinePlayers(this.plugin.getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()));
        this.plugin.getThisServer().setMemoryFree((int) (Runtime.getRuntime().freeMemory() / 1024 / 1024));
        this.plugin.getThisServer().setMemoryMax((int) (Runtime.getRuntime().maxMemory() / 1024 / 1024));
        this.plugin.getThisServer().setLastKeepAlive(System.currentTimeMillis());
        this.plugin.getThisServer().keepAlive();
    }
}
