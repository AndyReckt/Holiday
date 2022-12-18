package me.andyreckt.holiday.bukkit.server.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

import java.util.UUID;

@Getter @AllArgsConstructor
public class PlayerMessagePacket implements Packet {
    private UUID uuid;
    private String message;
}
