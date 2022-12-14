package me.andyreckt.holiday.core.util.redis.pubsub.subscribers;

import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.server.Server;
import me.andyreckt.holiday.core.util.redis.annotations.RedisListener;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.ServerKeepAlivePacket;

public class ServerKeepAliveSubscriber {

    @RedisListener
    public void onPacket(ServerKeepAlivePacket packet) {
        Server server = packet.getServer();
        server.setLastKeepAlive(System.currentTimeMillis());
        HolidayAPI.getUnsafeAPI().getServerManager().getServers().put(server.getServerId(), server);
        System.out.println("Received keep alive from " + server.getServerId());
    }

}
