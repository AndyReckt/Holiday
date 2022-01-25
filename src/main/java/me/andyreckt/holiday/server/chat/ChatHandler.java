package me.andyreckt.holiday.server.chat;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.StaffMessages;
import me.andyreckt.holiday.other.enums.StaffMessageType;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.player.punishments.PunishData;
import me.andyreckt.holiday.player.punishments.Punishment;
import me.andyreckt.holiday.player.punishments.PunishmentType;
import me.andyreckt.holiday.server.Server;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.Cooldown;
import me.andyreckt.holiday.utils.PunishmentUtils;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class ChatHandler {

    private Map<UUID, Cooldown> cooldownMap = new HashMap<>();

    private boolean chatMuted;
    private long chatDelay;
    private BasicConfigurationFile settings;

    private List<String> hardFilters;
    private List<String> filter;

    public ChatHandler(BasicConfigurationFile config, Server server) {
        this.settings = config;

        this.chatDelay = server.getChatDelay();
        this.chatMuted = server.isChatMuted();

        this.hardFilters = new ArrayList<>(settings.getStringList("FILTERS.HIGH.LIST"));
        this.filter = new ArrayList<>(settings.getStringList("FILTERS.LOW.LIST"));


    }


    public boolean isOnDelay(UUID uuid) {
        if (cooldownMap.containsKey(uuid)) {
            Cooldown cd = cooldownMap.get(uuid);
            if(cd.hasExpired()) {
                cooldownMap.remove(uuid);
                return false;
            } else return true;
        } else return false;
    }

    public boolean canChat(UUID uuid) {
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


    public boolean isFine(String message, Player player) {
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
            if (settings.getBoolean("FILTERS.HIGH.MUTE")) {
                new Punishment(ph.getConsoleProfile(),
                        ph.getByUUID(player.getUniqueId()),
                        PunishmentType.TEMP_MUTE,
                        settings.getString("FILTERS.HIGH.MUTEDURATION"),
                        settings.getString("FILTERS.HIGH.MUTEREASON").replace("<word>", filtered),
                        true);
            }

            if (settings.getBoolean("FILTERS.SENDTOSTAFF")) {
                String toSend = settings.getString("FILTERS.MESSAGE")
                                .replace("<server>", settings.getString("SERVER.NICENAME"))
                                .replace("<player>", ph.getByUUID(player.getUniqueId()).getNameWithColor())
                                .replace("<message>", message);
                Holiday.getInstance().getRedis().sendPacket(new StaffMessages.StaffMessagesPacket(toSend, StaffMessageType.STAFF));
            }

            return settings.getBoolean("FILTERS.HARD.ALLOWMESSAGE");
        }

        if (lowFilter) {
            if (settings.getBoolean("FILTERS.SENDTOSTAFF")) {
                String toSend = settings.getString("FILTERS.MESSAGE")
                        .replace("<server>", settings.getString("SERVER.NICENAME"))
                        .replace("<player>", ph.getByUUID(player.getUniqueId()).getNameWithColor())
                        .replace("<message>", message);
                Holiday.getInstance().getRedis().sendPacket(new StaffMessages.StaffMessagesPacket(toSend, StaffMessageType.STAFF));
            }
            return settings.getBoolean("FILTERS.LOW.ALLOWMESSAGE");
        }
        return true;
    }

    public void setChatMuted(boolean muted) {
        this.chatMuted = muted;
        Holiday.getInstance().getServerHandler().getThisServer().setChatMuted(muted);
        Holiday.getInstance().getServerHandler().save();
    }

    public void setChatDelay(long delay) {
        this.chatDelay = delay;
        Holiday.getInstance().getServerHandler().getThisServer().setChatDelay(delay);
        Holiday.getInstance().getServerHandler().save();
    }



}
