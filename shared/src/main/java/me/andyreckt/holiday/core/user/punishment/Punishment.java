package me.andyreckt.holiday.core.user.punishment;

import lombok.Getter;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.util.duration.TimeUtil;

import java.util.UUID;

@Getter
public class Punishment implements IPunishment {

    private final String id;

    private final UUID punished;
    private final PunishmentType type;

    private final long duration;
    private final String ip;

    private final UUID addedBy;
    private final long addedAt;
    private final String addedReason;

    UUID revokedBy = null;
    long revokedAt = TimeUtil.PERMANENT;
    String revokedReason = null;

    public Punishment(UUID punished, PunishmentType type, long duration, UUID addedBy, String addedReason) {
        this.punished = punished;
        this.type = type;
        this.duration = duration;
        this.addedBy = addedBy;
        this.addedReason = addedReason;

        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.addedAt = System.currentTimeMillis();
        this.ip = HolidayAPI.getUnsafeAPI().getProfile(punished).getIp();
    }


    @Override
    public long getRemainingTime() {
        return duration == TimeUtil.PERMANENT ? TimeUtil.PERMANENT : addedAt + duration - System.currentTimeMillis();
    }

    @Override
    public void revoke(UUID removedBy, String removedReason) {
        this.revokedBy = removedBy;
        this.revokedAt = System.currentTimeMillis();
        this.revokedReason = removedReason;
    }

    @Override
    public boolean isActive() {
        return revokedBy == null;
    }

    @Override
    public boolean check() {
        if (!isActive()) return false;
        if (duration == TimeUtil.PERMANENT) return false;
        if (getRemainingTime() <= 0) {
            revoke(UserProfile.getConsoleProfile().getUuid(), "Automatic");
            return true;
        }
        return false;
    }
}
