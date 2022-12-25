package me.andyreckt.holiday.bukkit.server.redis.subscriber;

import me.andyreckt.holiday.bukkit.server.redis.packet.KickPlayerPacket;
import me.andyreckt.holiday.bukkit.util.Logger;
import me.andyreckt.holiday.bukkit.util.other.Tasks;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import org.bukkit.Bukkit;

import java.util.UUID;

public class KickPlayerSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onReceive(KickPlayerPacket packet) {
        UUID uuid = packet.getPunishment().getPunished();
        Tasks.run(() -> {
            if (Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).kickPlayer(CC.translate(packet.getString()));
            }
        });
    }
}
