package me.andyreckt.holiday.punishments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.TimeUtil;

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


}
