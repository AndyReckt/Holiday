package me.andyreckt.holiday.core.util.redis.pubsub.subscribers;

import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.punishment.PunishmentManager;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.PunishmentUpdatePacket;

public class PunishmentUpdateSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onReceive(PunishmentUpdatePacket packet) {
        PunishmentManager punishmentManager = HolidayAPI.getUnsafeAPI().getPunishmentManager();
        punishmentManager.getPunishments().removeIf(punishment -> punishment.getId().equals(packet.getPunishment().getId()));
        punishmentManager.getPunishments().add(packet.getPunishment());
    }
}
