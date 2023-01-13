package me.andyreckt.holiday.bukkit.server.redis.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@RequiredArgsConstructor
public class CrossServerCommandPacket implements Packet {

    private final String command;
    private final String server;

}

