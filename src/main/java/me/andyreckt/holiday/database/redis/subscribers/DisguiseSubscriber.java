package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.DisguisePacket;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;

public class DisguiseSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onChange(DisguisePacket packet) {
        switch (packet.getType()) {
            case ADD:{
                if(!Holiday.getInstance().getDisguiseHandler().getUsedNames().contains(packet.getName())) Holiday.getInstance().getDisguiseHandler().getUsedNames().add(packet.getName().toLowerCase());
                break;
            }
            case REMOVE: Holiday.getInstance().getDisguiseHandler().getUsedNames().remove(packet.getName().toLowerCase());
        }
    }
}
