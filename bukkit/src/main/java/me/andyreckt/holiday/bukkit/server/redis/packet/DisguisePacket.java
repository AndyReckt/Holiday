package me.andyreckt.holiday.bukkit.server.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.disguise.DisguiseManager;
import me.andyreckt.holiday.core.user.disguise.Disguise;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@AllArgsConstructor
public class DisguisePacket implements Packet {
    private Disguise disguise;
    private boolean remove;

    @Override
    public void onReceive() {
        DisguiseManager disguiseManager = Holiday.getInstance().getDisguiseManager();
        if (remove) disguiseManager.removeDisguise(disguise.getUuid());
        else disguiseManager.addDisguise(disguise);
    }
}
