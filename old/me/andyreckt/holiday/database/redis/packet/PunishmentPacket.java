package me.andyreckt.holiday.database.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.other.enums.PunishmentSubType;
import me.andyreckt.holiday.player.punishments.PunishData;
import me.andyreckt.holiday.player.punishments.PunishmentType;
import me.andyreckt.holiday.utils.packets.Packet;

@Getter
@AllArgsConstructor
public class PunishmentPacket implements Packet {

    final PunishData punishData;
    final PunishmentSubType subType;
    final PunishmentType type;

}
