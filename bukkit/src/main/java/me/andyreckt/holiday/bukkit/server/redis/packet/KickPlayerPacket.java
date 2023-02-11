package me.andyreckt.holiday.bukkit.server.redis.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.bukkit.util.other.Tasks;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.punishment.Punishment;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;
import org.bukkit.Bukkit;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class KickPlayerPacket implements Packet {
    private final UUID uuid;
    private final String string;

    @Override
    public void onReceive() {
        Tasks.run(() -> {
            if (Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).kickPlayer(CC.translate(string));
            }
        });
    }
}
