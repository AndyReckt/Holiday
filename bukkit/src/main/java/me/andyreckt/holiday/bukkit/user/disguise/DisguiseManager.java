package me.andyreckt.holiday.bukkit.user.disguise;

import lombok.Getter;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.nms.INMS;
import me.andyreckt.holiday.bukkit.server.redis.packet.DisguisePacket;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.core.user.disguise.Disguise;
import me.andyreckt.holiday.core.util.http.Skin;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.Bukkit;
import org.redisson.api.RMap;

import java.util.*;
import java.util.stream.Collectors;

public class DisguiseManager {

    private final Holiday plugin;
    @Getter
    private final HashMap<UUID, Disguise> disguises;
    private final INMS nms;

    public DisguiseManager(Holiday plugin, INMS nms) {
        this.plugin = plugin;
        this.nms = nms;
        this.disguises = new HashMap<>();

        RMap<String, String> cache = plugin.getApi().getRedis().getClient().getMap("disguise-cache");
        for (Map.Entry<String, String> entry : cache.entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());
            Disguise disguise = GsonProvider.GSON.fromJson(entry.getValue(), Disguise.class);
            disguises.put(uuid, disguise);
        }
        for (String skinName : Locale.DISGUISE_SKINS.getStringList()) {
            Skin.getSkinByName(skinName).whenCompleteAsync((skin, throwable) -> {});
        }
    }

    public boolean isDisguised(UUID uuid) {
        return disguises.containsKey(uuid);
    }

    public boolean isDisguised(String name) {
        return disguises.values().stream().anyMatch(disguise -> disguise.getDisplayName().equalsIgnoreCase(name));
    }

    public Disguise getDisguise(UUID uuid) {
        return disguises.get(uuid);
    }

    public Disguise getDisguise(String name) {
        return disguises.values().stream().filter(disguise -> disguise.getDisplayName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void addDisguise(Disguise disguise) {
        disguises.put(disguise.getUuid(), disguise);
    }

    public void removeDisguise(UUID uuid) {
        disguises.remove(uuid);
    }

    public boolean isNameAvailable(String name) {
        for (Disguise disguise : disguises.values()) {
            if (disguise.getDisplayName().equalsIgnoreCase(name)) {
                return false;
            }
        }

        return disguises.values().stream().noneMatch(disguise -> disguise.getDisplayName().equalsIgnoreCase(name));
    }

    public void disguise(Disguise disguise, boolean sendRequest) {
        nms.disguise(disguise);
        if (sendRequest) {
            Profile profile = plugin.getApi().getProfile(disguise.getUuid());
            profile.setDisguise(disguise);
            plugin.getApi().saveProfile(profile);
            plugin.getApi().getRedis().getClient().getMap("disguise-cache").put(disguise.getUuid().toString(), GsonProvider.GSON.toJson(disguise));
            String toSend = Locale.DISGUISE_MESSAGE_STAFF.getString()
                    .replace("%server%", plugin.getThisServer().getServerName())
                    .replace("%player%", UserConstants.getNameWithColor(profile))
                    .replace("%name%",UserConstants.getDisplayNameWithColor(profile))
                    .replace("%skin%", disguise.getSkinName());
            plugin.getApi().getRedis().sendPacket(new DisguisePacket(disguise, false));
            plugin.getApi().getRedis().sendPacket(new BroadcastPacket(
                    toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.DISGUISES
            ));
        }
        UserConstants.reloadPlayer(disguise.getUuid());
    }

    public void unDisguise(Disguise disguise) {
        nms.unDisguise(disguise);
        Profile profile = plugin.getApi().getProfile(disguise.getUuid());
        profile.setDisguise(null);
        plugin.getApi().saveProfile(profile);
        plugin.getApi().getRedis().getClient().getMap("disguise-cache").remove(disguise.getUuid().toString());
        plugin.getApi().getRedis().sendPacket(new DisguisePacket(disguise, true));
        String toSend = Locale.DISGUISE_MESSAGE_STAFF_OFF.getString()
                .replace("%server%", plugin.getThisServer().getServerName())
                .replace("%player%", UserConstants.getNameWithColor(profile));
        plugin.getApi().getRedis().sendPacket(new BroadcastPacket(
                toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.DISGUISES
        ));
        UserConstants.reloadPlayer(disguise.getUuid());
    }

    public List<String> getUnusedNames() {
        return Locale.DISGUISE_NAMES.getStringList().stream().filter(this::isNameAvailable).collect(Collectors.toList());
    }

    public String getRandomName() {
        List<String> names = getUnusedNames();
        return names.get(new Random().nextInt(names.size() - 1));
    }


    public Skin getSkin(String name) {
        return Skin.getSkinByName(name).join();
    }

    public Skin getRandomSkin() {
        List<String> skins = new ArrayList<>(Skin.SKINS.keySet());
        return getSkin(skins.get(new Random().nextInt(skins.size() - 1)));
    }

}
