package me.andyreckt.holiday.player.punishments;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.PunishmentPacket;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.other.enums.PunishmentSubType;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.TimeUtil;
import org.bson.Document;

import java.util.UUID;

@SuppressWarnings("unchecked")
public class Punishment {
    public Punishment(Profile issuer, Profile punished, PunishmentType punishmentType, String reason, Boolean silent) {
        String punishId = UUID.randomUUID().toString().substring(0, 6);
        PunishData punishData = new PunishData(punishId, punished, punishmentType, issuer, reason, System.currentTimeMillis(), TimeUtil.PERMANENT, silent, punished.getIp());
        addPunishment(punishData);
        Holiday.getInstance().getRedis().sendPacket(new PunishmentPacket(punishData, PunishmentSubType.ADD));
    }

    public Punishment(Profile issuer, Profile punished, PunishmentType punishmentType, String duration, String reason, Boolean silent) {
        String punishId = UUID.randomUUID().toString().substring(0, 6);
        PunishData punishData = new PunishData(punishId, punished, punishmentType, issuer, reason, System.currentTimeMillis(), TimeUtil.getDuration(duration), silent, punished.getIp());
        addPunishment(punishData);
        Holiday.getInstance().getRedis().sendPacket(new PunishmentPacket(punishData, PunishmentSubType.ADD));
    }

    void addPunishment(PunishData punishData) {
        Document document = new Document("_id", punishData.getId())
                .append("punished", punishData.getPunished().getUuid().toString())
                .append("punishedName", punishData.getPunished().getName())
                .append("addedBy", punishData.getAddedBy().getUuid().toString())
                .append("type", punishData.getType().toString())
                .append("addedReason", punishData.getAddedReason())
                .append("addedAt", punishData.getAddedAt())
                .append("duration", punishData.getDuration())
                .append("silent", punishData.isSilent())
                .append("ip", punishData.getPunished().getIp())
                .append("removed", false)
                .append("removedAt", null)
                .append("removedBy", null)
                .append("removedReason", null);


        MongoUtils.submitToThread(() -> MongoUtils.getPunishmentsCollection().replaceOne(Filters.eq("_id", punishData.getId()), document, new ReplaceOptions().upsert(true)));
    }
}



