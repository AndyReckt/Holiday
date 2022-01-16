package me.andyreckt.holiday.database.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.packets.Packet;

@Getter @AllArgsConstructor
public class PermissionChangePacket implements Packet {

    Rank rank;

}
