package me.andyreckt.holiday.core.util.redis.pubsub.packets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.punishment.Punishment;
import me.andyreckt.holiday.core.user.punishment.PunishmentManager;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@RequiredArgsConstructor
public class PunishmentUpdatePacket implements Packet {
    private final Punishment punishment;

    @Override
    public void onReceive() {
        PunishmentManager punishmentManager = HolidayAPI.getUnsafeAPI().getPunishmentManager();
        punishmentManager.getPunishments().removeIf(punishment -> punishment.getId().equals(this.punishment.getId()));
        punishmentManager.getPunishments().add(punishment);
    }
}
