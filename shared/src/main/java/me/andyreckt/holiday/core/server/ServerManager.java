package me.andyreckt.holiday.core.server;

import lombok.Getter;
import me.andyreckt.holiday.api.server.IServer;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.ServerKeepAlivePacket;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.ServerUpdatePacket;

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
        api.runRedisCommand(redis -> {
            for (Map.Entry<String, String> serverEntry : redis.hgetAll("servers").entrySet()) {
                String serverId = serverEntry.getKey();
                String json = serverEntry.getValue();
                servers.put(serverId, GsonProvider.GSON.fromJson(json, Server.class));
            }
            return null;
        });
    }


    public IServer getServer(String serverId) {
        return this.servers.get(serverId);
    }

    public IServer getServer(UUID playerId) {
        return this.getServer(api.getOnlinePlayers().get(playerId));
    }

    public void keepAlive(Server server) {
        PacketHandler.send(new ServerKeepAlivePacket(server));
        CompletableFuture.runAsync(() -> api.runRedisCommand(redis -> {
            redis.hset("servers", server.getServerId(), GsonProvider.GSON.toJson(server));
            return null;
        }));
    }

    public void sendUpdate(Server server) {
        server.setLastKeepAlive(System.currentTimeMillis());
        PacketHandler.send(new ServerUpdatePacket(server));
        CompletableFuture.runAsync(() -> api.runRedisCommand(redis -> {
            redis.hset("servers", server.getServerId(), GsonProvider.GSON.toJson(server));
            return null;
        }));
    }
}
