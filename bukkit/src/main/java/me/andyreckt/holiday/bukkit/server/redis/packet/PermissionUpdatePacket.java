package me.andyreckt.holiday.bukkit.server.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionUpdatePacket implements Packet {

    private UUID uuid;

}
