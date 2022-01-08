package me.andyreckt.holiday.server.chat;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.player.punishments.PunishData;
import me.andyreckt.holiday.player.punishments.Punishment;
import me.andyreckt.holiday.player.punishments.PunishmentType;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.Cooldown;
import me.andyreckt.holiday.utils.PunishmentUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class ChatManager {

    public static Map<UUID, Cooldown> cooldownMap = new HashMap<>();

    public static boolean chatMuted = false;
    public static long chatDelay = 0;


    static String[] hardFilters = {"nigger", "coon", "niggers", "coons", "faggot", "niggr", "niggre", "nigrito"};
    static String[] filter = {"cunt", "negro", "anal", "anus", "beaner", "nazi", "paki", "niger", "gringo", "nigga"};


    public static boolean isOnDelay(UUID uuid) {
        if (cooldownMap.containsKey(uuid)) {
            Cooldown cd = cooldownMap.get(uuid);
            if(cd.hasExpired()) {
                cooldownMap.remove(uuid);
                return false;
            } else return true;
        } else return false;
    }

    public static boolean canChat(UUID uuid) {
        ProfileHandler ph = Holiday.getInstance().getProfileHandler();
        Profile profile = ph.getByUUID(uuid);

        if(PunishmentUtils.checkMuted(profile)) {
            PunishData data = PunishmentUtils.getMute(profile);
            if(data.isPermanent()) {
                profile.getPlayer().sendMessage(CC.translate("&cYou are muted. This mute is permanent."));
            } else {
                profile.getPlayer().sendMessage(CC.translate("&cYou are muted. This mute will expire in " + data.getNiceDuration() + "."));
            }
            return false;
        }

        if(profile.getHighestGrant().getRank().isStaff()) return true;

        if(chatMuted) {
            profile.getPlayer().sendMessage(CC.translate("&cThe global chat is currently muted."));
            return false;
        }

        if(isOnDelay(uuid)) {
            profile.getPlayer().sendMessage(CC.translate("&cThe global chat is currently slowed. You will be able to talk again in " + cooldownMap.get(uuid).getTimeLeft() + "s."));
            return false;
        }

        Cooldown cd = new Cooldown(chatDelay);
        cooldownMap.put(uuid, cd);

        return true;
    }


    public static boolean isFine(String message, Player player) {
        ProfileHandler ph = Holiday.getInstance().getProfileHandler();
        boolean hardFilter = false, lowFilter = false;
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
            //TODO CHECK CONFIG FOR MUTE
            new Punishment(ph.getConsoleProfile(),
                    ph.getByUUID(player.getUniqueId()),
                    PunishmentType.TEMP_MUTE, "6h",
                    "AutoMute (" + filtered + ")",
                    true);
            return false;
        }

        if (lowFilter) {
            //TODO SEND FILTERED MESSAGE

            //StaffUtils.Staff.sendFilteredMessage(Profile.getFromUUID(player.getUniqueId()).getNameWithColor(), message);
            /*Redis.getPidgin().sendPacket(
                    new BroadcastPacket("&d[Filtered] &5[" + Loader.getServerName() + "] "
                            + Profile.getFromUUID(player.getUniqueId()).getNameWithColor() + "&e: " + message, BroadcastType.STAFF));

             */
            return true;
        }
        return true;
    }



}
