package me.andyreckt.holiday.punishments;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.andyreckt.holiday.database.utils.MongoUtils;
import me.andyreckt.holiday.database.utils.RedisUtils;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.TimeUtil;
import org.bson.Document;

import java.util.UUID;

public class Punishment {

    public Punishment(Profile punished, Profile issuer, PunishmentType punishmentType, String reason, Boolean silent) {
        PunishData punishData = new PunishData(punished, punishmentType, issuer, reason, System.currentTimeMillis(), TimeUtil.PERMANENT, silent);
        RedisUtils.sendPunishmentAdd(punishData);
        addPunishment(punishData);
    }

    public Punishment(Profile punished, Profile issuer, PunishmentType punishmentType, String duration, String reason, Boolean silent) {
        PunishData punishData = new PunishData(punished, punishmentType, issuer, reason, System.currentTimeMillis(), TimeUtil.getDuration(duration), silent);
        RedisUtils.sendPunishmentAdd(punishData);
        addPunishment(punishData);
    }

    void addPunishment(PunishData punishData) {
        String punishId = UUID.randomUUID().toString().substring(0, 28);
        Document document = new Document("_id", punishId)
                .append("punished", punishData.getPunished().getUuid())
                .append("addedBy", punishData.getAddedBy().getUuid())
                .append("type", punishData.getType().toString())
                .append("addedReason", punishData.getAddedReason())
                .append("addedAt", punishData.getAddedAt())
                .append("duration", punishData.getDuration())
                .append("removed", false)
                .append("removedAt", null)
                .append("removedBy", null)
                .append("removedReason", null);

        MongoUtils.getExecutor().execute(() -> MongoUtils.getPunishmentsCollection().replaceOne(Filters.eq("_id", punishId), document, new ReplaceOptions().upsert(true)));
    }

}
