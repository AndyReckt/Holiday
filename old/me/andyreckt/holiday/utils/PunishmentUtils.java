package me.andyreckt.holiday.utils;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.punishments.PunishData;
import me.andyreckt.holiday.player.punishments.PunishmentHandler;
import me.andyreckt.holiday.player.punishments.PunishmentType;

import java.util.ArrayList;
import java.util.List;

public class PunishmentUtils {

    static PunishmentHandler handler = Holiday.getInstance().getPunishmentHandler();


    public static boolean checkBlacklisted(Profile profile) {
        return handler.getAllPunishmentsProfile(profile).stream()
                .filter(PunishData::isActive)
                .filter(data -> data.getPunished().getUuid() != null)
                .anyMatch(data -> data.getType().equals(PunishmentType.BLACKLIST));
    }

    public static PunishData getBlacklist(Profile profile) {
        if(checkBlacklisted(profile))
        return handler.getAllPunishmentsProfile(profile).stream()
                .filter(PunishData::isActive)
                .filter(data -> data.getPunished().getUuid() != null)
                .filter(data -> data.getType().equals(PunishmentType.BLACKLIST))
                .findFirst().get();
        else return null;
    }

    public static List<PunishData> blacklists(Profile profile) {
        List<PunishData> list = new ArrayList<>();
        handler.getAllPunishmentsProfile(profile).forEach(data -> {
            if(data.getType() == PunishmentType.BLACKLIST) {
                if (!list.contains(data)) list.add(data);
            }
        });

        list.sort((o1, o2) -> (int) (o2.getAddedAt() - o1.getAddedAt()));

        return list;
    }

    public static PunishData getIPBan(Profile profile) {
        if(checkIPBanned(profile))
            return handler.getAllPunishmentsProfile(profile).stream()
                    .filter(PunishData::isActive)
                    .filter(data -> data.getType().equals(PunishmentType.IP_BAN))
                    .findFirst().get();
        else return null;
    }

    public static boolean checkIPBanned(Profile profile) {
        return handler.getAllPunishmentsProfile(profile).stream()
                .filter(PunishData::isActive)
                .anyMatch(data -> data.getType().equals(PunishmentType.IP_BAN));
    }

    public static List<PunishData> ipbans(Profile profile) {
        List<PunishData> list = new ArrayList<>();
        handler.getAllPunishmentsProfile(profile).forEach(data -> {
            if(data.getType() == PunishmentType.IP_BAN) {
                if (!list.contains(data)) list.add(data);
            }
        });

        list.sort((o1, o2) -> (int) (o2.getAddedAt() - o1.getAddedAt()));

        return list;
    }

    public static PunishData getBan(Profile profile) {
        if(checkBanned(profile))
            return handler.getAllPunishmentsProfile(profile).stream()
                    .filter(PunishData::isActive)
                    .filter(data -> data.getType() == PunishmentType.BAN || data.getType() == PunishmentType.TEMP_BAN)
                    .findFirst().get();
        else return null;
    }

    public static boolean checkBanned(Profile profile) {
        return profile.getPunishments().stream()
                .filter(PunishData::isActive)
                .filter(data -> data.getPunished().getUuid().toString().equalsIgnoreCase(profile.getUuid().toString()))
                .anyMatch(data -> data.getType() == PunishmentType.BAN || data.getType() == PunishmentType.TEMP_BAN);
    }

    public static List<PunishData> bans(Profile profile) {
        List<PunishData> list = new ArrayList<>();
        handler.getAllPunishmentsProfile(profile).forEach(data -> {
            if(data.getPunished().getUuid().toString().equals(profile.getUuid().toString())){
                if(data.getType() == PunishmentType.BAN || data.getType() == PunishmentType.TEMP_BAN) {
                    if (!list.contains(data)) list.add(data);
                }
            }
        });

        list.sort((o1, o2) -> (int) (o2.getAddedAt() - o1.getAddedAt()));

        return list;
    }

    public static PunishData getMute(Profile profile) {
        if(checkMuted(profile))
            return handler.getAllPunishmentsProfile(profile).stream()
                    .filter(PunishData::isActive)
                    .filter(data -> data.getType().equals(PunishmentType.MUTE) || data.getType().equals(PunishmentType.TEMP_MUTE))
                    .findFirst().get();
        else return null;
    }

    public static boolean checkMuted(Profile profile) {
        return handler.getAllPunishmentsProfile(profile).stream()
                .filter(PunishData::isActive)
                .filter(data -> data.getPunished().getUuid().toString().equalsIgnoreCase(profile.getUuid().toString()))
                .anyMatch(data -> (data.getType() == PunishmentType.MUTE || data.getType() == PunishmentType.TEMP_MUTE));
    }

    public static List<PunishData> mutes(Profile profile) {
        List<PunishData> list = new ArrayList<>();

        handler.getAllPunishmentsProfile(profile).forEach(data -> {
            if(data.getPunished().getUuid().toString().equals(profile.getUuid().toString())){
                if(data.getType() == PunishmentType.MUTE || data.getType() == PunishmentType.TEMP_MUTE) {
                    if (!list.contains(data)) list.add(data);
                }
            }
        });

        list.sort((o1, o2) -> (int) (o2.getAddedAt() - o1.getAddedAt()));

        return list;
    }

    public static List<PunishData> actives() {
        List<PunishData> list = new ArrayList<>();

        handler.punishments().stream()
                .filter(PunishData::isActive)
                .forEach(list::add);

        return list;
    }

    public static List<PunishData> activesBlacklists() {
        List<PunishData> list = new ArrayList<>();

        handler.punishments().stream()
                .filter(PunishData::isActive)
                .filter(punishData -> punishData.getType() == PunishmentType.BLACKLIST)
                .forEach(list::add);

        return list;
    }

    public static List<PunishData> blacklists() {
        List<PunishData> list = new ArrayList<>();

        handler.punishments().stream()
                .filter(punishData -> punishData.getType() == PunishmentType.BLACKLIST)
                .forEach(list::add);

        return list;
    }

    public static List<PunishData> activesIpBans() {
        List<PunishData> list = new ArrayList<>();

        handler.punishments().stream()
                .filter(punishData -> punishData.getType() == PunishmentType.IP_BAN)
                .forEach(list::add);

        return list;
    }

    public static List<PunishData> ipbans() {
        List<PunishData> list = new ArrayList<>();

        handler.punishments().stream()
                .filter(punishData -> punishData.getType() == PunishmentType.IP_BAN)
                .forEach(list::add);

        return list;
    }

    public static List<PunishData> activesBans() {
        List<PunishData> list = new ArrayList<>();

        handler.punishments().stream()
                .filter(PunishData::isActive)
                .filter(punishData -> (punishData.getType() == PunishmentType.TEMP_BAN) || (punishData.getType() == PunishmentType.BAN))
                .forEach(list::add);

        return list;
    }

    public static List<PunishData> bans() {
        List<PunishData> list = new ArrayList<>();

        handler.punishments().stream()
                .filter(punishData -> (punishData.getType() == PunishmentType.TEMP_BAN) || (punishData.getType() == PunishmentType.BAN))
                .forEach(list::add);

        return list;
    }

    public static List<PunishData> activesMutes() {
        List<PunishData> list = new ArrayList<>();

        handler.punishments().stream()
                .filter(PunishData::isActive)
                .filter(punishData -> (punishData.getType() == PunishmentType.TEMP_MUTE) || (punishData.getType() == PunishmentType.MUTE))
                .forEach(list::add);

        return list;
    }

    public static List<PunishData> mutes() {
        List<PunishData> list = new ArrayList<>();

        handler.punishments().stream()
                .filter(punishData -> (punishData.getType() == PunishmentType.TEMP_MUTE) || (punishData.getType() == PunishmentType.MUTE))
                .forEach(list::add);

        return list;
    }

}
