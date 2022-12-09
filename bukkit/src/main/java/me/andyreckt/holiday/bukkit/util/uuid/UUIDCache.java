package me.andyreckt.holiday.bukkit.util.uuid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.core.util.redis.annotations.RedisListener;
import me.andyreckt.holiday.core.util.redis.annotations.RedisObject;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class UUIDCache {

    private static final Map<UUID, String> map = new ConcurrentHashMap<>();

    public UUIDCache() {
        Holiday.getInstance().getApi().getMidnight().getAllAsync("uuid-cache", String.class)
                .thenAccept(o -> o.forEach((key, value)
                        -> UUIDCache.map.put(UUID.fromString(key), (String) value)));
        Holiday.getInstance().getApi().getMidnight().registerListener(new UpdateUUIDCacheSubscriber());
        Holiday.getInstance().getApi().getMidnight().registerObject(UpdateUUIDCachePacket.class);
        new UUIDCacheListener(Holiday.getInstance());
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
        Holiday.getInstance().getApi().getMidnight().cache("uuid-cache", uuid.toString(), name);
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
            map.put(packet.getUuid(), packet.getName());
        }
    }

}

