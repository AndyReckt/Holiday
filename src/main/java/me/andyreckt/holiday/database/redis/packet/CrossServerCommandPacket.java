package me.andyreckt.holiday.database.redis.packet;

import me.andyreckt.holiday.utils.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class CrossServerCommandPacket implements Packet {

    String command;
    String server;

}
