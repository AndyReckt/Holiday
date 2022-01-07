package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.database.redis.packet.DisguisePacket;
import me.andyreckt.holiday.player.disguise.DisguiseManager;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;

public class DisguiseSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onChange(DisguisePacket packet) {
        switch (packet.getType()) {
            case ADD:{
                if(!DisguiseManager.usedNames.contains(packet.getName())) DisguiseManager.usedNames.add(packet.getName().toLowerCase());
                break;
            }
            case REMOVE: DisguiseManager.usedNames.remove(packet.getName().toLowerCase());
        }
    }
}
