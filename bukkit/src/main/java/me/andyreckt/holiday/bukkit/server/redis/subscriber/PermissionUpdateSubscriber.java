package me.andyreckt.holiday.bukkit.server.redis.subscriber;

import me.andyreckt.holiday.bukkit.server.redis.packet.PermissionUpdatePacket;
import me.andyreckt.holiday.bukkit.util.player.PermissionUtils;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PermissionUpdateSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onPacket(PermissionUpdatePacket packet) {
        if (packet.getUuid() == null) {
            Bukkit.getOnlinePlayers().stream()
                    .map(Player::getUniqueId)
                    .forEach(PermissionUtils::updatePermissions);
            return;
        }
        if (Bukkit.getPlayer(packet.getUuid()) != null) {
            PermissionUtils.updatePermissions(packet.getUuid());
        }
    }

}
