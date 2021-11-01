package me.andyreckt.holiday.punishments;

import com.mongodb.DBCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.andyreckt.holiday.database.Redis;
import me.andyreckt.holiday.database.packets.PunishmentPacket;
import me.andyreckt.holiday.database.utils.MongoUtils;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.TimeUtil;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Punishment {

    public Punishment(Profile punished, Profile issuer, PunishmentType punishmentType, String reason, Boolean silent) {
        PunishData punishData = new PunishData(punished, punishmentType, issuer, reason, System.currentTimeMillis(), TimeUtil.PERMANENT, silent);
        Redis.getPidgin().sendPacket(new PunishmentPacket(punishData));
        addPunishment(punishData);
    }

    public Punishment(Profile punished, Profile issuer, PunishmentType punishmentType, String duration, String reason, Boolean silent) {
        PunishData punishData = new PunishData(punished, punishmentType, issuer, reason, System.currentTimeMillis(), TimeUtil.getDuration(duration), silent);
        Redis.getPidgin().sendPacket(new PunishmentPacket(punishData));
        addPunishment(punishData);
    }

    void addPunishment(PunishData punishData) {
        String punishId = UUID.randomUUID().toString().substring(0, 28);
        Document document = new Document("_id", punishId)
                .append("punished", punishData.getPunished().getUuid().toString())
                .append("addedBy", punishData.getAddedBy().getUuid().toString())
                .append("type", punishData.getType().toString())
                .append("addedReason", punishData.getAddedReason())
                .append("addedAt", punishData.getAddedAt())
                .append("duration", punishData.getDuration())
                .append("silent", punishData.isSilent())
                .append("removed", false)
                .append("removedAt", null)
                .append("removedBy", null)
                .append("removedReason", null);

        MongoUtils.submitToThread(() -> MongoUtils.getPunishmentsCollection().replaceOne(Filters.eq("_id", punishId), document, new ReplaceOptions().upsert(true)));
    }

    public static List<Document> getAllPunishments() {
        List<Document> list = new ArrayList<>();

        MongoUtils.submitToThread(() -> {
            DBCursor cursor = (DBCursor) MongoUtils.getPunishmentsCollection().find();
            while(cursor.hasNext()) {
                list.add((Document) cursor.getQuery());
            }
        });

        return list;
    }

    public static List<Document> getAllPunishments(Player player) {
        List<Document> list = new ArrayList<>();

        MongoUtils.submitToThread(() -> {
            DBCursor cursor = (DBCursor) MongoUtils.getPunishmentsCollection().find(Filters.eq("punished", player.getUniqueId().toString()));
            while(cursor.hasNext()) {
                list.add((Document) cursor.getQuery());
            }
        });

        return list;
    }

    public static List<Document> getAllPunishments(UUID uuid) {
        List<Document> list = new ArrayList<>();

        MongoUtils.submitToThread(() -> {
            DBCursor cursor = (DBCursor) MongoUtils.getPunishmentsCollection().find(Filters.eq("punished", uuid.toString()));
            while(cursor.hasNext()) {
                list.add((Document) cursor.getQuery());
            }
        });

        return list;
    }

    public static List<Document> getAllPunishments(Profile profile) {
        List<Document> list = new ArrayList<>();

        MongoUtils.submitToThread(() -> {
            DBCursor cursor = (DBCursor) MongoUtils.getPunishmentsCollection().find(Filters.eq("punished", profile.getUuid().toString()));
            while(cursor.hasNext()) {
                list.add((Document) cursor.getQuery());
            }
        });

        return list;
    }





}



