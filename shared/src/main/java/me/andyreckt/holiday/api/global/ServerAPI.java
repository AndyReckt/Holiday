package me.andyreckt.holiday.api.global;

import me.andyreckt.holiday.core.server.Server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ServerAPI {

    boolean isOnline(UUID playerId);

    Map<UUID, String> getOnlinePlayers();

    CompletableFuture<HashMap<String, Server>> getServers();

    CompletableFuture<Server> getServer(String serverId);

    CompletableFuture<Server> getServer(UUID playerId);

}
