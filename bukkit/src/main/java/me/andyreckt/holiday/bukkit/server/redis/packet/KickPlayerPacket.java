package me.andyreckt.holiday.bukkit.server.redis.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.core.user.punishment.Punishment;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@RequiredArgsConstructor
public class KickPlayerPacket implements Packet {
    private final Punishment punishment;
    private final String string;
}
