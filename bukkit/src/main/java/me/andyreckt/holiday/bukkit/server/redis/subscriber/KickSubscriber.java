package me.andyreckt.holiday.bukkit.server.redis.subscriber;

import me.andyreckt.holiday.bukkit.server.redis.packet.KickPacket;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import org.bukkit.Bukkit;

import java.util.UUID;

public class KickSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onReceive(KickPacket packet) {
        UUID uuid = packet.getPunishment().getPunished();
        if (Bukkit.getPlayer(uuid) != null) {
            Bukkit.getPlayer(uuid).kickPlayer(CC.translate(packet.getString()));
        }
    }
}
