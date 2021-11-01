package me.andyreckt.holiday.database.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.punishments.PunishData;
import me.andyreckt.holiday.utils.packets.Packet;

@Getter
@AllArgsConstructor
public class PunishmentPacket implements Packet {

    final PunishData punishData;

}
