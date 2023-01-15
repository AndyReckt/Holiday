package me.andyreckt.holiday.bukkit.server.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;
import org.bukkit.Bukkit;

import java.util.UUID;

@Getter @AllArgsConstructor
public class PlayerMessagePacket implements Packet {
    private UUID uuid;
    private String message;

    @Override
    public void onReceive() {
        if (Bukkit.getPlayer(uuid) != null) {
            Bukkit.getPlayer(uuid).sendMessage(CC.translate(message));
        }
    }
}
