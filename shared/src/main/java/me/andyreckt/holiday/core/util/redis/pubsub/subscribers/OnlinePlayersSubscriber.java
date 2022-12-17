package me.andyreckt.holiday.core.util.redis.pubsub.subscribers;

import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.OnlinePlayersPacket;

public class OnlinePlayersSubscriber implements PacketListener {
    @IncomingPacketHandler
    public void onReceive(OnlinePlayersPacket packet) {
        HolidayAPI.getUnsafeAPI().setOnlinePlayers(packet.getPlayers());
    }
}
