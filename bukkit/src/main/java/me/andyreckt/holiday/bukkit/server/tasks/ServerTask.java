package me.andyreckt.holiday.bukkit.server.tasks;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.core.server.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;

public class ServerTask extends BukkitRunnable {

    private final Holiday plugin;
    private boolean x = true;

    public ServerTask(Holiday plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (x) {
            plugin.getApi().getServer(plugin.getThisServer().getServerId()).thenAccept(server -> {
                if (server == null) return;
                getServer().setChatMuted(server.isChatMuted());
                getServer().setChatDelay(server.getChatDelay());
                getServer().setWhitelisted(server.isWhitelisted());
                getServer().setWhitelistRank(server.getWhitelistRank());
                getServer().setWhitelistedPlayers(server.getWhitelistedPlayers());
            });
            this.x = false;
        }
        getServer().setJoinable(this.plugin.isJoinable());
        getServer().setTps(this.plugin.getServer().spigot().getTPS());
        getServer().setOnlinePlayers(this.plugin.getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()));
        getServer().setMaxPlayers(this.plugin.getServer().getMaxPlayers());
        getServer().setMemoryFree((int) (Runtime.getRuntime().freeMemory() / 1024 / 1024));
        getServer().setMemoryMax((int) (Runtime.getRuntime().maxMemory() / 1024 / 1024));
        getServer().setChatDelay(this.plugin.getChatManager().getChatDelay());
        getServer().setChatMuted(this.plugin.getChatManager().isChatMuted());
        getServer().keepAlive();
    }

    private Server getServer() {
        return this.plugin.getThisServer();
    }
}
