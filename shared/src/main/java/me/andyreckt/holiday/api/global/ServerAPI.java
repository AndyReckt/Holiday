package me.andyreckt.holiday.api.global;

import me.andyreckt.holiday.api.server.IServer;

import java.util.Map;
import java.util.UUID;

public interface ServerAPI {

    boolean isOnline(UUID playerId);

    Map<UUID, String> getOnlinePlayers();

    Map<String, IServer> getServers();

    IServer getServer(String serverId);

    IServer getServer(UUID playerId);

}
