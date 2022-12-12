package me.andyreckt.holiday.core.util.redis.pubsub.packets;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.util.redis.annotations.RedisObject;

@Getter
@RequiredArgsConstructor
@RedisObject(id = "BroadcastPacket")
public class BroadcastPacket {

    public final String message;
    public final String permission;

    public BroadcastPacket(String message) {
        this.message = message;
        this.permission = null;
    }

}
