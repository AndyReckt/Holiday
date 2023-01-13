package me.andyreckt.holiday.core.util.redis.pubsub.packets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.user.grant.Grant;
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
}
