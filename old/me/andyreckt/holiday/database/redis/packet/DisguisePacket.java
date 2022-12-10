package me.andyreckt.holiday.database.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.other.enums.DisguiseType;
import me.andyreckt.holiday.utils.packets.Packet;

@Getter
@AllArgsConstructor
public class DisguisePacket implements Packet {

    final String name;
    final DisguiseType type;

}