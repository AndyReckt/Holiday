package me.andyreckt.holiday.core.util.redis.pubsub.packets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

import java.util.HashMap;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class OnlinePlayersPacket implements Packet {
    private final HashMap<UUID, String> players;

    @Override
    public void onReceive() {
        HolidayAPI.getUnsafeAPI().setOnlinePlayers(players);
    }
}
