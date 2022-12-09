package me.andyreckt.holiday.core.util.redis.pubsub.subscribers;

import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.grant.GrantManager;
import me.andyreckt.holiday.core.util.redis.annotations.RedisListener;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.GrantUpdatePacket;

public class GrantUpdateSubscriber {

    @RedisListener
    public void onReceive(GrantUpdatePacket packet) {
        GrantManager grantManager = HolidayAPI.getUnsafeAPI().getGrantManager();
        grantManager.getGrants().removeIf(grant -> grant.getGrantId().equals(packet.getGrant().getGrantId()));

        if (packet.isDelete()) return;

        grantManager.getGrants().add(packet.getGrant());
    }
}
