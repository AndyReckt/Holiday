package me.andyreckt.holiday.server.chat;

import cc.teamfight.astria.player.Profile;
import cc.teamfight.astria.punishments.PunishData;
import cc.teamfight.astria.punishments.Punishment;
import cc.teamfight.astria.punishments.PunishmentType;
import cc.teamfight.astria.utils.CC;
import cc.teamfight.astria.utils.Cooldown;
import cc.teamfight.astria.utils.PunishmentUtils;
import cc.teamfight.astria.utils.StaffUtils;
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


    static String[] extremeFilters = {"jndi", "log4j", "kubernetes", "jvmrunargs"};
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
        Profile profile = Profile.getFromUUID(uuid);

        if(PunishmentUtils.checkMuted(profile)) {
            PunishData data = PunishmentUtils.getMute(profile);
            if(data.isPermanent()) {
                profile.getPlayer().sendMessage(CC.translate("&cYou are muted. This mute is permanent."));
            } else {
                profile.getPlayer().sendMessage(CC.translate("&cYou are muted. This mute will expire in " + data.getNiceDuration() + "."));
            }
            return false;
        }

        if(profile.getRank().isStaff()) return true;

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
        boolean hardFilter = false, lowFilter = false, extrmeFilter = false;
        String filtered = "";
        String replacedMessage = message.toLowerCase()
                .replace("@", "a")
                .replace("3", "e")
                .replace("0", "o")
                .replace("4", "a")
                .replace("1", "i")
                .replace("5", "s")
                .replaceAll("[^a-z0-9 ]", "");

        for (String s : extremeFilters) {
            if (replacedMessage.contains(s)) {
                extrmeFilter = true;
                filtered = s;
                break;
            }
        }
        if (!extrmeFilter)
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

        if (extrmeFilter) {
            new Punishment(Profile.getConsoleProfile(),
                    Profile.getFromUUID(player.getUniqueId()),
                    PunishmentType.TEMP_BAN, "1d",
                    "Trying to use Log4j exploit (" + filtered + ")",
                    true);
            return false;
        }

        if (hardFilter) {
            new Punishment(Profile.getConsoleProfile(),
                    Profile.getFromUUID(player.getUniqueId()),
                    PunishmentType.TEMP_MUTE, "6h",
                    "AutoMute (" + filtered + ")",
                    true);
            return false;
        }

        if (lowFilter) {
            StaffUtils.Staff.sendFilteredMessage(Profile.getFromUUID(player.getUniqueId()).getNameWithColor(), message);
            /*Redis.getPidgin().sendPacket(
                    new BroadcastPacket("&d[Filtered] &5[" + Loader.getServerName() + "] "
                            + Profile.getFromUUID(player.getUniqueId()).getNameWithColor() + "&e: " + message, BroadcastType.STAFF));

             */
            return true;
        }
        return true;
    }



}
