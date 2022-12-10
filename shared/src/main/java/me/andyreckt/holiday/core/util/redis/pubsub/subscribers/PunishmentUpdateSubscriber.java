package me.andyreckt.holiday.core.util.redis.pubsub.subscribers;

import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.punishment.PunishmentManager;
import me.andyreckt.holiday.core.util.redis.annotations.RedisListener;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.PunishmentUpdatePacket;

public class PunishmentUpdateSubscriber {

    @RedisListener
    public void onReceive(PunishmentUpdatePacket packet) {
        PunishmentManager punishmentManager = HolidayAPI.getUnsafeAPI().getPunishmentManager();
        punishmentManager.getPunishments().removeIf(punishment -> punishment.getId().equals(packet.getPunishment().getId()));
        punishmentManager.getPunishments().add(packet.getPunishment());
    }
}
