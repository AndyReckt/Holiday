package me.andyreckt.holiday.database.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.other.enums.BroadcastType;
import me.andyreckt.holiday.utils.packets.Packet;

@Getter
@AllArgsConstructor
public class BroadcastPacket implements Packet {

    final String message;
    final BroadcastType type;

}