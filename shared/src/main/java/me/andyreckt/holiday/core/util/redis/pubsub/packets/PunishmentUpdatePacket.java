package me.andyreckt.holiday.core.util.redis.pubsub.packets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.user.punishment.Punishment;
import me.andyreckt.holiday.core.util.redis.annotations.RedisObject;

@Getter
@RequiredArgsConstructor
@RedisObject(id = "GrantUpdatePacket")
public class PunishmentUpdatePacket {
    private final Punishment punishment;
}
