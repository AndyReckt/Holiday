package me.andyreckt.holiday.core.util.redis.pubsub.packets;

import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.server.Server;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@RequiredArgsConstructor
public class ServerUpdatePacket implements Packet {

    private final Server server;

    @Override
    public void onReceive() {
        HolidayAPI.getUnsafeAPI().getServerManager().getServers().put(server.getServerId(), server);
        if (HolidayAPI.getUnsafeAPI().getServerUpdateConsumer() != null) {
            HolidayAPI.getUnsafeAPI().getServerUpdateConsumer().accept(server);
        }
    }
}
