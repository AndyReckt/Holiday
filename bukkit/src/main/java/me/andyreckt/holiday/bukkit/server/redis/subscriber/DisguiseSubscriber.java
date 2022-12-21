package me.andyreckt.holiday.bukkit.server.redis.subscriber;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.DisguisePacket;
import me.andyreckt.holiday.bukkit.user.disguise.Disguise;
import me.andyreckt.holiday.bukkit.user.disguise.DisguiseManager;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;

public class DisguiseSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onPacket(DisguisePacket packet) {
        DisguiseManager disguiseManager = Holiday.getInstance().getDisguiseManager();
        if (packet.isRemove()) disguiseManager.removeDisguise(packet.getDisguise().getUuid());
        else disguiseManager.addDisguise(packet.getDisguise());
    }
}
