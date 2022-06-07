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

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
@RequiredArgsConstructor
public class    PunishData {

    private final String id;
    private final Profile punished;
    private final PunishmentType type;
    private final Profile addedBy;
    private final String addedReason;
    private final long addedAt, duration;
    private final boolean silent;
    private final String ip;


    @Nullable private Profile removedBy;
    @Nullable private String removedReason;
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
        return this.removed ? "Removed" : isPermanent() ? "Permanent" : TimeUtil.getDuration(Math.abs((System.currentTimeMillis() - (this.addedAt + this.duration))));
    }

    public String getDurationString() {
        if(duration == -1) return ChatColor.DARK_PURPLE + "Permanent";
        return TimeUtil.getDuration(duration);
    }


    public void save() {
        PunishmentHandler ph = Holiday.getInstance().getPunishmentHandler();
        ph.updateCache(getId(), this);
        MongoUtils.submitToThread(() -> MongoUtils.getPunishmentsCollection().replaceOne(Filters.eq("_id", getId()), toBson(), new ReplaceOptions().upsert(true)));
        Holiday.getInstance().getRedis().sendPacket(new PunishmentPacket(this, PunishmentSubType.EDIT));
    }

    public Document toBson() {

        String idd;
        if (addedBy == null || addedBy.getUuid() == null) idd = Holiday.getInstance().getProfileHandler().getConsoleProfile().getUuid().toString();
        else idd = addedBy.getUuid().toString();

        String rBy;
        if ((removedBy == null || removedBy.getUuid() == null) && removed) rBy = new Profile().getUuid().toString();
        else if ((removedBy == null || removedBy.getUuid() == null) && !removed) rBy = "null";
        else if (removed && removedBy.getUuid() != null) rBy = removedBy.getUuid().toString();
        else rBy = Holiday.getInstance().getProfileHandler().getConsoleProfile().getUuid().toString();
        return new Document("_id", id)
                .append("punished", punished.getUuid().toString())
                .append("addedBy", idd)
                .append("type", type.getName())
                .append("addedReason", addedReason)
                .append("addedAt", addedAt)
                .append("duration", duration)
                .append("ip", ip)
                .append("silent", silent)
                .append("removed", removed)
                .append("removedAt", removedAt)
                .append("removedBy", rBy)
                .append("removedReason", removedReason);
    }


}
