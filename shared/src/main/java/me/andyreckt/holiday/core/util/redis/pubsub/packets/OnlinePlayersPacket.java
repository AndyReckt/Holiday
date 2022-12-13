package me.andyreckt.holiday.core.util.redis.pubsub.packets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.util.redis.annotations.RedisObject;

import java.util.HashMap;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@RedisObject(id = "OnlinePlayersPacket")
public class OnlinePlayersPacket {
    private final HashMap<UUID, String> players;
}
