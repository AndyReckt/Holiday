package me.andyreckt.holiday.bukkit.server.redis.subscriber;

import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.Bukkit;

public class BroadcastSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onReceive(BroadcastPacket packet) {
        if(packet.getPermission() != null) {
            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.hasPermission(packet.getPermission()))
                    .forEach(player -> player.sendMessage(CC.translate(packet.getMessage())));
        } else {
            Bukkit.broadcastMessage(CC.translate(packet.getMessage()));
        }
    }

}
