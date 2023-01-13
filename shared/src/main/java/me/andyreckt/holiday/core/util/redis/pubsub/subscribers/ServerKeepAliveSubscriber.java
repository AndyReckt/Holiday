package me.andyreckt.holiday.core.util.redis.pubsub.subscribers;

import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.server.Server;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.ServerKeepAlivePacket;

public class ServerKeepAliveSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onPacket(ServerKeepAlivePacket packet) {
        Server server = packet.getServer();
        server.setLastKeepAlive(System.currentTimeMillis());
        HolidayAPI.getUnsafeAPI().getServerManager().getServers().put(server.getServerId(), server);
    }

}
