package me.andyreckt.holiday.database.redis.packet;


import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.other.enums.GrantType;
import me.andyreckt.holiday.player.grant.Grant;
import me.andyreckt.holiday.utils.packets.Packet;

@Getter
@AllArgsConstructor
public class GrantPacket implements Packet {

    Grant grant;

}
