package me.andyreckt.holiday.player.punishments;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.database.redis.packet.PunishmentPacket;
import me.andyreckt.holiday.other.enums.PunishmentSubType;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.utils.TimeUtil;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
@RequiredArgsConstructor
public class PunishData {

    private final String id;
    private final Profile punished;
    private final PunishmentType type;
    private final Profile addedBy;
    private final String addedReason;
    private final long addedAt, duration;
    private final boolean silent;
    private final String ip;

    private Profile removedBy;
    private String removedReason;
    private long removedAt;
    private boolean removed;

    public String getRemovedReason() {
        if(removedReason == null) {
            return "Automatic";
        }
        return removedReason;
    }

    public Profile getRemovedBy() {
        if(removedBy == null) {
            return Holiday.getInstance().getProfileHandler().getConsoleProfile();
        }
        return removedBy;
    }

    public boolean isActive() {
        return !this.removed && (isPermanent() || System.currentTimeMillis() < this.addedAt + this.duration);
    }

    public boolean isPermanent() {
        return this.duration == -1;
    }

    public boolean isExpired() {
        return !isPermanent() && System.currentTimeMillis() >= this.addedAt + this.duration;
    }

    public String getNiceDuration() {
        return isPermanent() ? "Permanent" : this.removed ? "Enlevé" : TimeUtil.formatDuration(Math.abs((System.currentTimeMillis() - (this.addedAt + this.duration))));
    }

    public String getDurationString() {
        if(duration == -1) return ChatColor.DARK_PURPLE + "Permanent";
        return TimeUtil.formatDuration(duration);
    }


    public void save() {
        PunishmentHandler ph = Holiday.getInstance().getPunishmentHandler();
        ph.updateCache(getId(), this);
        MongoUtils.submitToThread(() -> MongoUtils.getPunishmentsCollection().replaceOne(Filters.eq("_id", getId()), toBson(), new ReplaceOptions().upsert(true)));
        Holiday.getInstance().getRedis().sendPacket(new PunishmentPacket(this, PunishmentSubType.EDIT));
    }

    public Document toBson() {
        return new Document("_id", getId())
                .append("punished", getPunished().getUuid().toString())
                .append("addedBy", getAddedBy().getUuid().toString())
                .append("type", getType().getName())
                .append("addedReason", getAddedReason())
                .append("addedAt", getAddedAt())
                .append("duration", getDuration())
                .append("ip", getIp())
                .append("silent", isSilent())
                .append("removed", isRemoved())
                .append("removedAt", getRemovedAt())
                .append("removedBy", getRemovedBy().getUuid().toString())
                .append("removedReason", getRemovedReason());
    }


}
