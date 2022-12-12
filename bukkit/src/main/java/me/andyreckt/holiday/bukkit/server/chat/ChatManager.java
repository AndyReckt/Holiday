package me.andyreckt.holiday.bukkit.server.chat;

import lombok.Getter;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.other.Cooldown;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.user.punishment.Punishment;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class ChatManager {

    private final Holiday plugin;

    private final Map<UUID, Cooldown> cooldownMap = new HashMap<>();

    private final List<String> hardFilters;
    private final List<String> filter;

    public ChatManager(Holiday plugin) {
        this.plugin = plugin;
        this.hardFilters = Locale.FILTER_HIGH_LIST.getStringList();
        this.filter = Locale.FILTER_LOW_LIST.getStringList();
    }

    public long getChatDelay() {
        return plugin.getThisServer().getChatDelay();
    }

    public boolean isChatMuted() {
        return plugin.getThisServer().isChatMuted();
    }

    public boolean isOnDelay(UUID uuid) {
        if (!cooldownMap.containsKey(uuid)) return false;
        Cooldown cd = cooldownMap.get(uuid);
        if (cd.hasExpired()) {
            cooldownMap.remove(uuid);
            return false;
        } else return true;
    }

    public boolean isFine(String message, Player player) {
        boolean hardFilter = false, lowFilter = false;
        Profile profile = plugin.getApi().getProfile(player.getUniqueId());

        String filtered = "";
        String replacedMessage = message.toLowerCase()
                .replace("@", "a")
                .replace("3", "e")
                .replace("0", "o")
                .replace("4", "a")
                .replace("1", "i")
                .replace("5", "s")
                .replaceAll("[^a-z0-9 ]", "");

        for (String s : hardFilters) {
            if (replacedMessage.contains(s)) {
                hardFilter = true;
                filtered = s;
                break;
            }
        }
        if (!hardFilter)
            for (String s : filter) {
                if (replacedMessage.contains(s)) {
                    lowFilter = true;
                    filtered = s;
                    break;
                }
            }
        if (hardFilter) {
            if (Locale.FILTER_HIGH_MUTE.getBoolean()) {
                Punishment punishment = new Punishment(player.getUniqueId(),
                        IPunishment.PunishmentType.MUTE,
                        TimeUtil.getDuration(Locale.FILTER_HIGH_MUTE_DURATION.getString()),
                        UserProfile.getConsoleProfile().getUuid(),
                        Locale.FILTER_HIGH_MUTE_REASON.getString().replace("%word%", filtered)
                );
                plugin.getApi().addPunishment(punishment);
            }

            if (Locale.FILTER_SEND.getBoolean()) {
                String toSend = Locale.FILTER_MESSAGE.getString()
                        .replace("%server%", Locale.SERVER_NAME.getString())
                        .replace("%player%", plugin.getNameWithColor(profile))
                        .replace("%message%", message);
                //TODO: send staff message
            }

            return Locale.FILTER_HIGH_ALLOW.getBoolean();
        }

        if (lowFilter) {
            if (Locale.FILTER_SEND.getBoolean()) {
                String toSend = Locale.FILTER_MESSAGE.getString()
                        .replace("%server%", Locale.SERVER_NAME.getString())
                        .replace("%player%", plugin.getNameWithColor(profile))
                        .replace("%message%", message);
                //TODO: send staff message
            }
            return Locale.FILTER_LOW_ALLOW.getBoolean();
        }
        return true;
    }



}
