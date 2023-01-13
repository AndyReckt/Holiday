package me.andyreckt.holiday.bukkit.server.redis.subscriber;

import me.andyreckt.holiday.bukkit.server.redis.packet.MessagePacket;
import me.andyreckt.holiday.bukkit.server.redis.packet.PlayerMessagePacket;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import org.bukkit.Bukkit;

public class PlayerMessageSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onReceive(PlayerMessagePacket packet) {
        if (Bukkit.getPlayer(packet.getUuid()) != null) {
            Bukkit.getPlayer(packet.getUuid()).sendMessage(CC.translate(packet.getMessage()));
        }
    }
}
