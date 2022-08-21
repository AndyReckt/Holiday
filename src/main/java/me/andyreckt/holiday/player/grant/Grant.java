package me.andyreckt.holiday.player.grant;

import com.google.gson.reflect.TypeToken;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.database.redis.packet.GrantPacket;
import me.andyreckt.holiday.other.enums.UpdateType;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.TimeUtil;
import org.bson.Document;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Grant {
    public static final Type GRANTS = new TypeToken<Grant>() {
    }.getType();

    private UUID uuid = UUID.randomUUID();

    private String rankId = "";
    private String reason = "", issuedOn = "", removedOn = "", removedReason = "";
    private UUID target, issuedBy, removedBy;

    private boolean active = true;
    private long duration = TimeUtil.PERMANENT, issuedAt = 0, removedAt = 0;

    public static Grant fromJson(String json) {
        return Holiday.getInstance().getGson().fromJson(json, GRANTS);
    }

    public static String toJson(Grant grant) {
        return Holiday.getInstance().getGson().toJson(grant, GRANTS);
    }

    public String toJson() {
        return Holiday.getInstance().getGson().toJson(this, GRANTS);
    }

    public Document getDocument() {
        return Document.parse(toJson());
    }

    public Rank getRank() {
        return Holiday.getInstance().getRankHandler().getFromId(UUID.fromString(rankId));
    }

    public int getPriority() {
        return getRank() == null ? 0 : getRank().getPriority();
    }

    public boolean hasExpired() {
        if (duration == TimeUtil.PERMANENT) return false;
        return (issuedAt + duration) <= System.currentTimeMillis();
    }

    public void save() {
        MongoUtils.submitToThread(() -> MongoUtils.getGrantCollection().replaceOne(Filters.eq("_id", uuid.toString()), getDocument(), new ReplaceOptions().upsert(true)));
        Holiday.getInstance().getRedis().sendPacket(new GrantPacket(this, UpdateType.UPDATE));
    }

    public void delete() {
        MongoUtils.submitToThread(() -> {
            MongoUtils.getGrantCollection().deleteOne(Filters.eq("_id", uuid.toString()));
        });
        Holiday.getInstance().getRedis().sendPacket(new GrantPacket(this, UpdateType.DELETE));
    }

    public boolean isActive() {
        if (hasExpired()) {
            setRemovedBy(Holiday.getInstance().getProfileHandler().getConsoleProfile().getUuid());
            setRemovedOn(Holiday.getInstance().getServerHandler().getThisServer().getName());
            setRemovedAt(System.currentTimeMillis());
            setRemovedReason("Expired");
            setActive(false);
            save();
        }
        return !hasExpired() && active;
    }

    public long getRemainingTime() {
        if (duration == TimeUtil.PERMANENT) return TimeUtil.PERMANENT;
        else return (duration + issuedAt) - System.currentTimeMillis();
    }
}