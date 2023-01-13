package me.andyreckt.holiday.api.global;

import me.andyreckt.holiday.api.user.IGrant;

import java.util.List;
import java.util.UUID;

public interface GrantAPI {

    List<IGrant> getGrants();

    List<IGrant> getGrants(UUID uuid);

    IGrant getGrantFromId(UUID grantId);

    void refreshGrants();

    void saveGrant(IGrant grant);

    void revokeGrant(IGrant grant, UUID revokedBy, String revokedOn, String revokedReason);
}
