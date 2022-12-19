package me.andyreckt.holiday.bukkit.server.redis.subscriber;

import me.andyreckt.holiday.bukkit.server.redis.packet.PermissionUpdatePacket;
import me.andyreckt.holiday.bukkit.util.player.PermissionUtils;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import org.bukkit.Bukkit;

public class PermissionUpdateSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onPacket(PermissionUpdatePacket packet) {
        if (packet.getUuid() == null) {
            Bukkit.getOnlinePlayers().forEach(player -> PermissionUtils.updatePermissions(player.getUniqueId()));
            return;
        }
        if (Bukkit.getPlayer(packet.getUuid()) != null) {
            PermissionUtils.updatePermissions(packet.getUuid());
        }
    }

}
