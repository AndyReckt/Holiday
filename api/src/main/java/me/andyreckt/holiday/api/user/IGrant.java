package me.andyreckt.holiday.api.user;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public interface IGrant {

        @SerializedName("_id")
        UUID getGrantId();

        IRank getRank();

        UUID getUser();
        UUID getIssuedBy();
        UUID getRevokedBy();

        String getReason();
        String getRevokeReason();

        String getIssuedOn();
        String getRevokedOn();

        long getDuration();
        long getIssuedAt();
        long getRevokedAt();

        long getRemainingTime();

        boolean isActive();

        void revoke(UUID revokedBy, String revokedOn, String revokeReason);
        boolean check();

}
