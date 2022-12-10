package me.andyreckt.holiday.database.redis.packet;

import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessagePacket implements Packet {

    final Profile target, sender;
    final String message;

}