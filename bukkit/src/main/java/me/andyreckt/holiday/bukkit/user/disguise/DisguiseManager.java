package me.andyreckt.holiday.bukkit.user.disguise;

import lombok.Getter;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.nms.INMS;
import me.andyreckt.holiday.bukkit.server.redis.packet.DisguisePacket;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.other.Tasks;
import me.andyreckt.holiday.core.user.disguise.Disguise;
import me.andyreckt.holiday.core.util.http.Skin;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
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

        plugin.getApi().runRedisCommand(redis -> {
            for (Map.Entry<String, String> entry : redis.hgetAll("disguise-cache").entrySet()) {
                Disguise disguise = GsonProvider.GSON.fromJson(entry.getValue(), Disguise.class);
                disguises.put(UUID.fromString(entry.getKey()), disguise);
            }
            return null;
        });
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

    public boolean isPlayerOnline(String name) {
        return plugin.getApi().getOnlinePlayers().keySet()
                .stream()
                .map(plugin.getApi()::getProfile)
                .noneMatch(profile -> profile.getName().equalsIgnoreCase(name) || profile.getDisplayName().equalsIgnoreCase(name));
    }

    public void disguise(Disguise disguise, boolean sendRequest) {
        nms.disguise(disguise);
        if (sendRequest) {
            Profile profile = plugin.getApi().getProfile(disguise.getUuid());
            profile.setDisguise(disguise);
            plugin.getApi().saveProfile(profile);

            plugin.getApi().runRedisCommand(redis -> {
                redis.hset("disguise-cache", disguise.getUuid().toString(), GsonProvider.GSON.toJson(disguise));
                return null;
            });

            String toSend = Locale.DISGUISE_MESSAGE_STAFF.getString()
                    .replace("%server%", plugin.getThisServer().getServerName())
                    .replace("%player%", UserConstants.getNameWithColor(profile))
                    .replace("%name%",UserConstants.getDisplayNameWithColor(profile))
                    .replace("%skin%", disguise.getSkinName());
            PacketHandler.send(new DisguisePacket(disguise, false));
            PacketHandler.send(new BroadcastPacket(
                    toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.DISGUISES
            ));
        }
        Tasks.runLater(() -> UserConstants.reloadPlayer(disguise.getUuid()), 5L);
    }

    public void unDisguise(Disguise disguise) {
        nms.unDisguise(disguise);
        Profile profile = plugin.getApi().getProfile(disguise.getUuid());
        profile.setDisguise(null);
        plugin.getApi().saveProfile(profile);

        plugin.getApi().runRedisCommand(redis -> {
            redis.hdel("disguise-cache", disguise.getUuid().toString());
            return null;
        });

        PacketHandler.send(new DisguisePacket(disguise, true));
        String toSend = Locale.DISGUISE_MESSAGE_STAFF_OFF.getString()
                .replace("%server%", plugin.getThisServer().getServerName())
                .replace("%player%", UserConstants.getNameWithColor(profile));
        PacketHandler.send(new BroadcastPacket(
                toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.DISGUISES
        ));
        Tasks.runLater(() -> UserConstants.reloadPlayer(disguise.getUuid()), 5L);
    }

    public List<String> getUnusedNames() {
        return Locale.DISGUISE_NAMES.getStringList().stream().filter(this::isNameAvailable).collect(Collectors.toList());
    }

    public String getRandomName() {
        List<String> names = getUnusedNames();
        return names.get(plugin.getRandom().nextInt(names.size() - 1));
    }


    public Skin getSkin(String name) {
        return Skin.getSkinByName(name).join();
    }

    public Skin getRandomSkin() {
        List<String> skins = new ArrayList<>(Skin.SKINS.keySet());
        return getSkin(skins.get(plugin.getRandom().nextInt(skins.size() - 1)));
    }

}
