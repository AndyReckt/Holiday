package me.andyreckt.holiday.bukkit.server.tasks;

import me.andyreckt.holiday.api.server.IServer;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.core.server.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;

public class ServerTask extends BukkitRunnable {

    private final Holiday plugin;
    private boolean serverStarting = true;

    public ServerTask(Holiday plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (serverStarting) {
            IServer server = plugin.getApi().getServer(plugin.getThisServer().getServerId());
            if (server == null) return;
            getServer().setChatMuted(server.isChatMuted());
            getServer().setChatDelay(server.getChatDelay());
            getServer().setWhitelisted(server.isWhitelisted());
            getServer().setWhitelistRank(server.getWhitelistRank());
            getServer().setWhitelistedPlayers(server.getWhitelistedPlayers());
            getServer().keepAlive();
            this.serverStarting = false;
            return;
        }
        getServer().setJoinable(this.plugin.isJoinable());
        getServer().setTps(this.plugin.getServer().spigot().getTPS());
        getServer().setOnlinePlayers(this.plugin.getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()));
        getServer().setMemoryFree((int) (Runtime.getRuntime().freeMemory() / 1024 / 1024));
        getServer().setMemoryMax((int) (Runtime.getRuntime().maxMemory() / 1024 / 1024));
        getServer().setLastKeepAlive(System.currentTimeMillis());
        getServer().keepAlive();
    }

    private Server getServer() {
        return this.plugin.getThisServer();
    }
}
