package me.andyreckt.holiday.database.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.utils.packets.Packet;


@Getter @AllArgsConstructor
public class ServerStartPacket implements Packet {

    final String serverName;

}
