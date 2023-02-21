package me.andyreckt.holiday.api.global;

import me.andyreckt.holiday.api.user.IPunishment;

import java.util.List;
import java.util.UUID;

public interface PunishmentAPI {

    void revokePunishment(IPunishment punishment, UUID revokedBy, String revokedReason, String revokedOn);

    List<IPunishment> getPunishments(UUID uniqueId);

    List<IPunishment> getPunishments();

    void savePunishment(IPunishment punishment);

    void refreshPunishments();

    IPunishment getPunishment(String id);


}
