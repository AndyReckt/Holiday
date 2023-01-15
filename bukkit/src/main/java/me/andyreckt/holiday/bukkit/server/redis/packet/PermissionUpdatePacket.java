package me.andyreckt.holiday.bukkit.server.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.andyreckt.holiday.bukkit.util.player.PermissionUtils;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionUpdatePacket implements Packet {

    private UUID uuid;

    @Override
    public void onReceive() {
        if (uuid == null) {
            Bukkit.getOnlinePlayers().stream()
                    .map(Player::getUniqueId)
                    .forEach(PermissionUtils::updatePermissions);
            return;
        }
        if (Bukkit.getPlayer(uuid) != null) {
            PermissionUtils.updatePermissions(uuid);
        }
    }
}
