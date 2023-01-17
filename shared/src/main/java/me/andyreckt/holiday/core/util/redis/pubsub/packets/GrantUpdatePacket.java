package me.andyreckt.holiday.core.util.redis.pubsub.packets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.grant.Grant;
import me.andyreckt.holiday.core.user.grant.GrantManager;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@RequiredArgsConstructor
public class GrantUpdatePacket implements Packet {
    private final Grant grant;
    private final boolean delete;

    public GrantUpdatePacket(Grant grant) {
        this.grant = grant;
        this.delete = false;
    }


    @Override
    public void onReceive() {
        GrantManager grantManager = HolidayAPI.getUnsafeAPI().getGrantManager();
        grantManager.getGrants().removeIf(grant -> grant.getGrantId().equals(this.grant.getGrantId()));
        if (delete) return;
        grantManager.getGrants().add(grant);
    }
}
