package me.andyreckt.holiday.core.util.redis.pubsub.packets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.user.punishment.Punishment;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@RequiredArgsConstructor
public class PunishmentUpdatePacket implements Packet {
    private final Punishment punishment;
}
