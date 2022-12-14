package me.andyreckt.holiday.core.util.redis.pubsub.subscribers;

import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.redis.annotations.RedisListener;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.ServerKeepAlivePacket;

public class ServerKeepAliveSubscriber {

    @RedisListener
    public void onPacket(ServerKeepAlivePacket packet) {
        HolidayAPI.getUnsafeAPI().getServerManager().getServers().put(packet.getServer().getServerId(), packet.getServer());
    }

}
