package me.andyreckt.holiday.core.user.grant;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IGrant;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.util.duration.Duration;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


@Getter @Setter
public class Grant implements IGrant {

    @SerializedName("_id")
    private final UUID grantId;
    private final UUID user;
    private final UUID rankId;
    private final UUID issuedBy;
    private UUID revokedBy;

    private final String reason;
    private String revokeReason;

    private final String issuedOn;
    private String revokedOn;

    private final Duration duration;
    private final long issuedAt;
    private long revokedAt;

    private boolean active = true;

    public Grant(UUID user, IRank rank, UUID issuedBy, String reason, String issuedOn, Duration duration) {
        this.grantId = UUID.randomUUID();
        this.user = user;
        this.rankId = rank.getUuid();
        this.issuedBy = issuedBy;
        this.reason = reason;
        this.duration = duration;
        this.issuedOn = issuedOn;
        this.issuedAt = System.currentTimeMillis();
    }

    private boolean hasExpired() {
        if (duration.isPermanent()) return false;
        return (issuedAt + duration.get()) <= System.currentTimeMillis();
    }

    @Override
    public long getDuration() {
        return duration.get();
    }

    public Duration getDurationObject() {
        return duration;
    }

    @Override
    public IRank getRank() {
        return HolidayAPI.getUnsafeAPI().getRank(rankId);
    }

    @Override
    public long getRemainingTime() {
        return duration.isPermanent() ? duration.get() : issuedAt + duration.get() - System.currentTimeMillis();
    }

    @Override
    public boolean isActive() {
        return !hasExpired() && active;
    }

    @Override
    public void revoke(UUID revokedBy, String revokedOn, String revokeReason) {
        this.revokedBy = revokedBy;
        this.revokedOn = revokedOn;
        this.revokedAt = System.currentTimeMillis();
        this.revokeReason = revokeReason;
        this.active = false;
    }

    @Override
    public boolean check() {
        if (!hasExpired()) return false;
        if (!active) return false;
        revoke(UserProfile.getConsoleProfile().getUuid(), "Automatic", "Expired");
        return true;
    }

}
