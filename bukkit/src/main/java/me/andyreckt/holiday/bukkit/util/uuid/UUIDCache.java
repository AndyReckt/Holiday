package me.andyreckt.holiday.bukkit.util.uuid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class UUIDCache {

    private static final Map<UUID, String> map = new ConcurrentHashMap<>();
    private final Holiday plugin;

    public UUIDCache(Holiday plugin) {
        this.plugin = plugin;

        plugin.getApi().runRedisCommand(redis -> {
            for (Map.Entry<String, String> entry : redis.hgetAll("uuid-cache").entrySet()) {
                map.put(UUID.fromString(entry.getKey()), entry.getValue());
            }
            return null;
        });

        new UUIDCacheListener(plugin);
    }

    public UUID uuid(String name) {
        for (Map.Entry<UUID, String> entry : map.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String name(UUID uuid) {
        return map.get(uuid);
    }

    public void update(final UUID uuid, final String name) {
        plugin.getExecutor().execute(() -> {
            map.put(uuid, name);
            plugin.getApi().runRedisCommand(redis -> {
                redis.hset("uuid-cache", uuid.toString(), name);
                return null;
            });
        });
        PacketHandler.send(new UpdateUUIDCachePacket(uuid, name));
    }

    @RequiredArgsConstructor @Getter
    public static class UpdateUUIDCachePacket implements Packet {
        private final UUID uuid;
        private final String name;

        @Override
        public void onReceive() {
            map.put(uuid, name);
        }
    }

}

