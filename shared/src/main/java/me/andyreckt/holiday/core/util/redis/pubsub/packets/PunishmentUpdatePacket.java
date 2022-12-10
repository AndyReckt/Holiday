package me.andyreckt.holiday.core.util.redis.pubsub.packets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.user.grant.Grant;
import me.andyreckt.holiday.core.util.redis.annotations.RedisObject;

@Getter
@RequiredArgsConstructor
@RedisObject(id = "GrantUpdatePacket")
public class GrantUpdatePacket {
    private final Grant grant;
    private final boolean delete;

    public GrantUpdatePacket(Grant grant) {
        this.grant = grant;
        this.delete = false;
    }
}
