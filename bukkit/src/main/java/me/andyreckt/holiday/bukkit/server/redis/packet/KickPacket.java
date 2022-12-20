package me.andyreckt.holiday.bukkit.server.redis.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class KickPacket implements Packet {
    private final IPunishment punishment;
    private final String string;
}
