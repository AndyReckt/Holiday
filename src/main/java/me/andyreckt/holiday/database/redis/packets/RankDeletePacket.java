package me.andyreckt.holiday.database.redis.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.utils.packets.Packet;

import java.util.UUID;


@Getter
@AllArgsConstructor
public class RankDeletePacket implements Packet {

    final UUID uuid;
}
