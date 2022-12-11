package me.andyreckt.holiday.core.server;

import me.andyreckt.holiday.core.HolidayAPI;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ServerManager {

    private final HolidayAPI api;

    public ServerManager(HolidayAPI api) {
        this.api = api;
    }

    public CompletableFuture<Server> getServer(String serverId) {
        return api.getMidnight().getAsync("servers", serverId, Server.class);
    }

    public CompletableFuture<Server> getServer(UUID playerId) {
        return api.getMidnight().getAsync("servers", api.getOnlinePlayers().get(playerId), Server.class);
    }

    public CompletableFuture<HashMap<String, Server>> getServers() {
        return api.getMidnight().getAllAsync("servers", Server.class);
    }

    public void keepAlive(Server server) {
        api.getMidnight().cache("servers", server.getServerId(), server);
    }
}
