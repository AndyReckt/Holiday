package me.andyreckt.holiday.core.user.punishment;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.util.duration.Duration;
import me.andyreckt.holiday.core.util.duration.TimeUtil;

import java.util.UUID;

@Getter
public class Punishment implements IPunishment {

    @SerializedName("_id")
    private final String id;

    private final UUID punished;
    private final PunishmentType type;

    private final Duration duration;
    private final String ip;

    private final UUID addedBy;
    private final long addedAt;
    private final String addedReason;
    private String addedOn;

    private UUID revokedBy = null;
    private long revokedAt = TimeUtil.PERMANENT;
    private String revokedReason = null;
    private String revokedOn = null;

    public Punishment(UUID punished, PunishmentType type, Duration duration, UUID addedBy, String addedReason, String addedOn) {
        this.punished = punished;
        this.type = type;
        this.duration = duration;
        this.addedBy = addedBy;
        this.addedReason = addedReason;
        this.addedOn = addedOn;

        this.id = UUID.randomUUID().toString().replace("-", "").substring(0, 7);
        this.addedAt = System.currentTimeMillis();
        this.ip = HolidayAPI.getUnsafeAPI().getProfile(punished).getIp();
    }

    @Override
    public long getDuration() {
        return duration.get();
    }

    public Duration getDurationObject() {
        return duration;
    }

    public Duration getRemainingDuration() {
        return new Duration(getRemainingTime());
    }

    @Override
    public long getRemainingTime() {
        return duration.isPermanent() ? TimeUtil.PERMANENT : addedAt + duration.get() - System.currentTimeMillis();
    }

    @Override
    public void revoke(UUID removedBy, String removedReason, String removedOn) {
        this.revokedBy = removedBy;
        this.revokedAt = System.currentTimeMillis();
        this.revokedReason = removedReason;
        this.revokedOn = removedOn;
    }

    @Override
    public boolean isActive() {
        return revokedBy == null;
    }

    @Override
    public boolean check() {
        if (!isActive()) return false;
        if (duration.isPermanent()) return false;
        if (getRemainingTime() <= 0) {
            this.revoke(UserProfile.getConsoleProfile().getUuid(), "Expired", "Automatic");
            return true;
        }
        return false;
    }

    @Override
    public boolean isPermanent() {
        return duration.isPermanent();
    }

    public void postProcess() {
        if (revokedOn == null) {
            revokedOn = "$undefined";
        }

        if (addedOn == null) {
            addedOn = "$undefined";
        }
    }
}
