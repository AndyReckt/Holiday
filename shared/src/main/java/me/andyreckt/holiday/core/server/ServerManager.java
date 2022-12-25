package me.andyreckt.holiday.core.server;

import lombok.Getter;
import me.andyreckt.holiday.api.server.IServer;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.ServerKeepAlivePacket;
import org.redisson.api.RMap;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager {

    private final HolidayAPI api;

    @Getter
    private final Map<String, IServer> servers;

    public ServerManager(HolidayAPI api) {
        this.api = api;
        this.servers = new ConcurrentHashMap<>();
        this.load();
    }

    private void load() {
        RMap<String, String> serverMap = api.getRedis().getClient().getMap("server-map");
        for (Map.Entry<String, String> serverEntry : serverMap.entrySet()) {
            String serverId = serverEntry.getKey();
            String json = serverEntry.getValue();
            servers.put(serverId, GsonProvider.GSON.fromJson(json, Server.class));
        }
    }


    public IServer getServer(String serverId) {
        return this.servers.get(serverId);
    }

    public IServer getServer(UUID playerId) {
        return this.getServer(api.getOnlinePlayers().get(playerId));
    }

    public void keepAlive(Server server) {
        api.getRedis().sendPacket(new ServerKeepAlivePacket(server));
        CompletableFuture.runAsync(() -> {
            api.getRedis().getClient().getMap("server-map").put(server.getServerId(), GsonProvider.GSON.toJson(server));
        });
    }
}
