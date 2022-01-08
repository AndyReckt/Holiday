package me.andyreckt.holiday.database.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.other.enums.RankType;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.packets.Packet;

@Getter
@AllArgsConstructor

public class RankPacket implements Packet {

    Rank rank;
    RankType updateType;

}
