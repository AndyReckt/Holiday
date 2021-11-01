package me.andyreckt.holiday.punishments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.TimeUtil;
import org.bson.Document;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class PunishData {

    final Profile punished;
    final PunishmentType type;
    final Profile addedBy;
    final String addedReason;
    final long addedAt, duration;
    final boolean silent;

    Profile removedBy;
    String removedReason;
    long removedAt;
    boolean removed;



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
        return isPermanent() ? "Permanent" : this.removed ? "Removed" : TimeUtil.formatDuration(Math.abs((System.currentTimeMillis() - (this.addedAt + this.duration))));
    }

    public static PunishData getFromDocument(Document document) {

        Profile punished = Profile.getFromUUID(UUID.fromString(document.getString("punished")));
        Profile issuer = Profile.getFromUUID(UUID.fromString(document.getString("addedBy")));
        PunishmentType type = PunishmentType.getByName(document.getString("type"));
        String reason = document.getString("addedReason");
        Date addedAt = document.getDate("addedAt");
        long duration = document.getLong("duration");
        boolean silent = document.getBoolean("silent");
        boolean removed = document.getBoolean("removed");
        long removedAt = document.getLong("removedAt");
        Profile removedBy = Profile.getFromUUID(UUID.fromString(document.getString("removedBy")));
        String removedReason = document.getString("removedReason");

        PunishData data = new PunishData(punished, type, issuer, reason, addedAt.getTime(), duration, silent);
        data.setRemoved(removed);
        data.setRemovedAt(removedAt);
        data.setRemovedBy(removedBy);
        data.setRemovedReason(removedReason);

        return data;
    }



}
