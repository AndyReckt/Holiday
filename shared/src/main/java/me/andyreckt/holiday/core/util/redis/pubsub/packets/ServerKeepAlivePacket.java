package me.andyreckt.holiday.core.util.redis.pubsub.packets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.server.Server;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@RequiredArgsConstructor
public class ServerKeepAlivePacket implements Packet {

    private final Server server;

    @Override
    public void onReceive() {
        server.setLastKeepAlive(System.currentTimeMillis());
        HolidayAPI.getUnsafeAPI().getServerManager().getServers().put(server.getServerId(), server);
    }
}
