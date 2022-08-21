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
    private long duration = TimeUtil.PERMANENT, issuedAt = 0L, removedAt = 0L;

    public Document getDocument() {
        Document document = new Document();
        document.put("rankId", rankId);
        document.put("reason", reason);
        document.put("issuedOn", issuedOn);
        document.put("removedOn", removedOn);
        document.put("removedReason", removedReason);
        document.put("target", target.toString());
        document.put("issuedBy", issuedBy.toString());
        if (removedBy != null) {
            document.put("removedBy", removedBy.toString());
        }
        document.put("active", active);
        document.put("duration", duration);
        document.put("issuedAt", issuedAt);
        document.put("removedAt", removedAt);
        return document;
    }

    public static Grant update(Document document) {
        Grant grant = new Grant();
        grant.setRankId(document.getString("rankId"));
        grant.setReason(document.getString("reason"));
        grant.setIssuedOn(document.getString("issuedOn"));
        grant.setRemovedOn(document.getString("removedOn"));
        grant.setRemovedReason(document.getString("removedReason"));
        grant.setTarget(UUID.fromString(document.getString("target")));
        grant.setIssuedBy(UUID.fromString(document.getString("issuedBy")));
        if (document.containsKey("removedBy")) {
            grant.setRemovedBy(UUID.fromString(document.getString("removedBy")));
        }
        grant.setActive(document.getBoolean("active", false));
        grant.setDuration(document.getLong("duration"));
        grant.setIssuedAt(document.getLong("issuedAt"));
        grant.setRemovedAt(document.getLong("removedAt"));
        return grant;
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

    public void expire() {
        setRemovedBy(Holiday.getInstance().getProfileHandler().getConsoleProfile().getUuid());
        setRemovedOn(Holiday.getInstance().getServerHandler().getThisServer().getName());
        setRemovedAt(System.currentTimeMillis());
        setRemovedReason("Expired");
        setActive(false);
    }

    public boolean isActive() {
        if (hasExpired()) {
            expire();
            save();
        }
        return !hasExpired() && active;
    }

    public long getRemainingTime() {
        if (duration == TimeUtil.PERMANENT) return TimeUtil.PERMANENT;
        else return (duration + issuedAt) - System.currentTimeMillis();
    }
}