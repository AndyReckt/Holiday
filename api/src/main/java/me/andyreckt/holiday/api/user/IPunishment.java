package me.andyreckt.holiday.api.user;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.text.WordUtils;

import java.util.UUID;

public interface IPunishment {

    @SerializedName("_id")
    String getId();

    UUID getPunished();
    PunishmentType getType();

    UUID getAddedBy();
    long getAddedAt();
    String getAddedReason();
    String getAddedOn();

    UUID getRevokedBy();
    long getRevokedAt();
    String getRevokedReason();
    String getRevokedOn();

    long getDuration();
    long getRemainingTime();

    String getIp();

    void revoke(UUID removedBy, String removedReason, String removedOn);

    boolean isActive();
    boolean check();
    boolean isPermanent();



    enum PunishmentType {
        MUTE, BAN, IP_BAN, BLACKLIST;

        public String getName() {
            if (this.name().equals("IP_BAN")) {
                return "IP Ban";
            }
            return WordUtils.capitalizeFully(this.name());
        }
    }
}
