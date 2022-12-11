package me.andyreckt.holiday.bukkit.server.tasks;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.core.server.Server;
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
        getServer().setJoinable(this.plugin.isJoinable());
        getServer().setTps(this.plugin.getServer().spigot().getTPS());
        getServer().setOnlinePlayers(this.plugin.getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()));
        getServer().setMaxPlayers(this.plugin.getServer().getMaxPlayers());
        getServer().setMemoryFree((int) (Runtime.getRuntime().freeMemory() / 1024 / 1024));
        getServer().setMemoryMax((int) (Runtime.getRuntime().maxMemory() / 1024 / 1024));
        //TODO: Whitelist & Chat updates
        getServer().keepAlive();
    }

    private Server getServer() {
        return this.plugin.getThisServer();
    }
}
