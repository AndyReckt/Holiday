package me.andyreckt.holiday.api.user;

import org.apache.commons.lang3.text.WordUtils;

import java.util.UUID;

public interface IPunishment {

    String getId();

    UUID getPunished();
    PunishmentType getType();

    UUID getAddedBy();
    long getAddedAt();
    String getAddedReason();

    UUID getRevokedBy();
    long getRevokedAt();
    String getRevokedReason();

    long getDuration();
    long getRemainingTime();

    String getIp();

    void revoke(UUID removedBy, String removedReason);

    boolean isActive();
    boolean check();



    enum PunishmentType {
        MUTE, BAN, IP_BAN, BLACKLIST;

        public String getName() {
            if(this.name().equals("IP_BAN")) {
                return "IP Ban";
            }
            return WordUtils.capitalizeFully(this.name());
        }
    }
}
