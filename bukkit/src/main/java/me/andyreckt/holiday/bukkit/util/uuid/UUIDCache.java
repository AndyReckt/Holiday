package me.andyreckt.holiday.bukkit.util.uuid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import org.redisson.api.RMap;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class UUIDCache {

    private static final Map<UUID, String> map = new ConcurrentHashMap<>();
    private final Holiday plugin;

    public UUIDCache(Holiday plugin) {
        this.plugin = plugin;

        RMap<String, String> cache = plugin.getApi().getRedis().getClient().getMap("uuid-cache");
        for (Map.Entry<String, String> cacheEntry : cache.entrySet()) {
            UUID uuid = UUID.fromString(cacheEntry.getKey());
            String name = cacheEntry.getValue();
            map.put(uuid, name);
        }

        this.plugin.getApi().getRedis().registerAdapter(UpdateUUIDCachePacket.class, new UpdateUUIDCacheSubscriber());
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
        map.put(uuid, name);
        this.plugin.getApi().getRedis().getClient().getMap("uuid-cache").put(uuid.toString(), name);
        this.plugin.getApi().getRedis().sendPacket(new UpdateUUIDCachePacket(uuid, name));
    }

    @RequiredArgsConstructor @Getter
    public static class UpdateUUIDCachePacket implements Packet {
        private final UUID uuid;
        private final String name;
    }

    public static class UpdateUUIDCacheSubscriber implements PacketListener {
        @IncomingPacketHandler
        public void onPacket(UpdateUUIDCachePacket packet) {
            map.put(packet.getUuid(), packet.getName());
        }
    }

}

