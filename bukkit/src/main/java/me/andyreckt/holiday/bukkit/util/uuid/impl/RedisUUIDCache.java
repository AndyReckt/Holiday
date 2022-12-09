package me.andyreckt.holiday.bukkit.util.uuid.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.Logger;
import me.andyreckt.holiday.bukkit.util.other.Tasks;
import me.andyreckt.holiday.bukkit.util.uuid.UUIDCache;
import me.andyreckt.holiday.core.util.redis.annotations.RedisListener;
import me.andyreckt.holiday.core.util.redis.annotations.RedisObject;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RedisUUIDCache implements UUIDCache {

    private static final Map<UUID, String> uuidToName = new ConcurrentHashMap<>();
    private static final Map<String, UUID> nameToUuid = new ConcurrentHashMap<>();

    public RedisUUIDCache() {
        Holiday.getInstance().getApi().runRedisCommand(redis -> {
            Map<String, String> cache = redis.hgetAll("UUIDCache");
            for (Map.Entry<String, String> cacheEntry : cache.entrySet()) {
                UUID uuid = UUID.fromString(cacheEntry.getKey());
                String name = cacheEntry.getValue();
                uuidToName.put(uuid, name);
                nameToUuid.put(name.toLowerCase(), uuid);
            }
            return null;
        });
        Holiday.getInstance().getApi().getMidnight().registerListener(new UpdateUUIDCacheSubscriber());
        Holiday.getInstance().getApi().getMidnight().registerObject(UpdateUUIDCachePacket.class);
    }

    @Override
    public UUID uuid(String name) {
        return nameToUuid.get(name.toLowerCase());
    }

    @Override
    public String name(UUID uuid) {
        return uuidToName.get(uuid);
    }

    @Override
    public void ensure(UUID uuid) {
        if (String.valueOf(this.name(uuid)).equals("null")) {
            Logger.warn(uuid + " didn't have a cached name.");
        }
    }

    @Override
    public void update(final UUID uuid, final String name) {
        uuidToName.put(uuid, name);
        for (Map.Entry<String, UUID> entry : new HashMap<>(nameToUuid).entrySet()) {
            if (!entry.getValue().equals(uuid)) continue;
            nameToUuid.remove(entry.getKey());
        }
        nameToUuid.put(name.toLowerCase(), uuid);
        Tasks.runAsync(() -> {
            Holiday.getInstance().getApi().runRedisCommand(redis -> {
                redis.hset("UUIDCache", uuid.toString(), name);
                return null;
            });
        });
        Holiday.getInstance().getApi().getMidnight().sendObject(new UpdateUUIDCachePacket(uuid, name));
    }

    @RequiredArgsConstructor @Getter
    @RedisObject(id = "UUIDCacheUpdate")
    public static class UpdateUUIDCachePacket {
        private final UUID uuid;
        private final String name;
    }

    public static class UpdateUUIDCacheSubscriber {
        @RedisListener
        public void onPacket(UpdateUUIDCachePacket packet) {
            nameToUuid.put(packet.getName().toLowerCase(), packet.getUuid());
            uuidToName.put(packet.getUuid(), packet.getName());
        }
    }

}

