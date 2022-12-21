package me.andyreckt.holiday.bukkit.user.disguise;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import org.redisson.api.RMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DisguiseManager {

    private final Holiday plugin;
    private final HashMap<UUID, Disguise> disguises;

    public DisguiseManager(Holiday plugin) {
        this.plugin = plugin;
        this.disguises = new HashMap<>();

        RMap<String, String> cache = plugin.getApi().getRedis().getClient().getMap("disguise-cache");
        for (Map.Entry<String, String> entry : cache.entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());
            Disguise disguise = GsonProvider.GSON.fromJson(entry.getValue(), Disguise.class);
            disguises.put(uuid, disguise);
        }
    }

    public boolean isDisguised(UUID uuid) {
        return disguises.containsKey(uuid);
    }

    public Disguise getDisguise(UUID uuid) {
        return disguises.get(uuid);
    }

    public boolean isNameAvailable(String name) {
        for (Disguise disguise : disguises.values()) {
            if (disguise.getDisplayName().equalsIgnoreCase(name)) {
                return false;
            }
        }

        return disguises.values().stream().noneMatch(disguise -> disguise.getDisplayName().equalsIgnoreCase(name));
    }
}
