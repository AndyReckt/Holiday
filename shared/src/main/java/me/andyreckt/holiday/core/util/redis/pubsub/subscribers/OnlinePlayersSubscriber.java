package me.andyreckt.holiday.core.util.redis.pubsub.subscribers;

import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.redis.annotations.RedisListener;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.OnlinePlayersPacket;

public class OnlinePlayersSubscriber {
    @RedisListener
    public void onReceive(OnlinePlayersPacket packet) {
        HolidayAPI.getUnsafeAPI().setOnlinePlayers(packet.getPlayers());
    }
}
