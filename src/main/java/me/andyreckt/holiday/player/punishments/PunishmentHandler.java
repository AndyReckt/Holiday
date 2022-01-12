package me.andyreckt.holiday.player.punishments;

import com.mongodb.Block;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.*;

public class PunishmentHandler {

    private Map<String, PunishData> punishments;

    public PunishmentHandler() {
        init();
    }

    public List<Document> getAllPunishments() {
        List<Document> list = new ArrayList<>();
        MongoUtils.getPunishmentsCollection()
                .find()
                .forEach((Block<Document>) list::add);
        return list;
    }

    private void init() {
        punishments = new HashMap<>();
        getAllPunishments().forEach(pun -> {
            PunishData data = getFromDocument(pun);
            updateCache(data.getId(), data);
        });
    }

    public List<PunishData> getAllPunishmentsPlayer(Player player) {
        List<PunishData> list = new ArrayList<>();
        punishments().stream().filter(data -> data.getPunished().getUuid() == player.getUniqueId()).forEach(list::add);
        return list;
    }

    public List<PunishData> getAllPunishmentsPlayer(String name) {
        List<PunishData> list = new ArrayList<>();
        punishments().stream().filter(data -> data.getPunished().getName().equalsIgnoreCase(name)).forEach(list::add);
        return list;
    }

    public List<PunishData> getAllPunishmentsUuid(UUID uuid) {
        List<PunishData> list = new ArrayList<>();
        punishments().stream().filter(data -> data.getPunished().getUuid() == uuid).forEach(list::add);
        return list;
    }

    public List<PunishData> getAllActivePunishments() {
        List<PunishData> list = new ArrayList<>();
        punishments().stream().filter(PunishData::isActive).forEach(list::add);
        return list;
    }

    public List<PunishData> getAllPunishmentsIp(String ip) {
        List<PunishData> list = new ArrayList<>();
        punishments().stream().filter(data -> data.getPunished().getIp().equalsIgnoreCase(ip)).forEach(list::add);
        return list;
    }

    public List<PunishData> getAllPunishmentsProfile(Profile profile) {
        List<PunishData> list = new ArrayList<>();
        punishments().stream().filter(data -> data.getPunished().getIp().equalsIgnoreCase(profile.getIp())).forEach(list::add);
        punishments().stream().filter(data -> data.getPunished().getName().equalsIgnoreCase(profile.getName())).forEach(list::add);
        punishments().stream().filter(data -> data.getPunished().getUuid() == profile.getUuid()).forEach(list::add);
        return list;
    }


    public boolean cacheContains(String id) {
        return punishments.containsKey(id);
    }

    public void updateCache(String id, PunishData data) {
        punishments.put(id, data);
    }

    public void clearCache() {
        punishments.clear();
    }

    public List<PunishData> punishments() {
        return new ArrayList<>(punishments.values());
    }

    public PunishData getFromId(String id) {
        if (cacheContains(id)) return punishments.get(id);
        else return null;
    }

    public PunishData getFromDocument(Document document) {
        ProfileHandler ph = Holiday.getInstance().getProfileHandler();

        String id = document.getString("_id");
        Profile punished = ph.getByUUIDFor5Minutes(UUID.fromString(document.getString("punished")));
        Profile issuer = ph.getByUUIDFor5Minutes(UUID.fromString(document.getString("addedBy")));
        PunishmentType type = PunishmentType.getByName(document.getString("type"));
        String reason = document.getString("addedReason");
        long addedAt = document.getLong("addedAt");
        long duration = document.getLong("duration");
        String ip = document.getString("ip");
        boolean silent = document.getBoolean("silent");
        boolean removed = document.getBoolean("removed");
        PunishData data = new PunishData(id, punished, type, issuer, reason, addedAt, duration, silent, ip);
        data.setRemoved(removed);
        if (removed) {
            long removedAt = document.getLong("removedAt");
            Profile removedBy = ph.getByUUIDFor5Minutes(UUID.fromString(document.getString("removedBy")));
            String removedReason = document.getString("removedReason");
            data.setRemovedAt(removedAt);
            data.setRemovedBy(removedBy);
            data.setRemovedReason(removedReason);
        }
        return data;
    }


}
