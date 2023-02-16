package me.andyreckt.holiday.core.util.redis.pubsub.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProfileDeletePacket implements Packet {
    private UUID uuid;

    @Override
    public void onReceive() {
        HolidayAPI.getUnsafeAPI().getUserManager().getProfiles().remove(uuid);
    }
}
