package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.DisguisePacket;
import me.andyreckt.holiday.player.disguise.impl.v1_7.DisguiseHandler_1_7;
import me.andyreckt.holiday.player.disguise.impl.v1_8.DisguiseHandler_1_8;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;

public class DisguiseSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onChange(DisguisePacket packet) {

        if (Holiday.getInstance().getDisguiseHandler() instanceof DisguiseHandler_1_7) {
            switch (packet.getType()) {
                case ADD: {
                    if (!((DisguiseHandler_1_7) Holiday.getInstance().getDisguiseHandler()).getUsedNames().contains(packet.getName()))
                        ((DisguiseHandler_1_7) Holiday.getInstance().getDisguiseHandler()).getUsedNames().add(packet.getName().toLowerCase());
                    break;
                }
                case REMOVE:
                    ((DisguiseHandler_1_7) Holiday.getInstance().getDisguiseHandler()).getUsedNames().remove(packet.getName().toLowerCase());
            }
        } else {
            switch (packet.getType()) {
                case ADD: {
                    if (!((DisguiseHandler_1_8) Holiday.getInstance().getDisguiseHandler()).getUsedNames().contains(packet.getName()))
                        ((DisguiseHandler_1_8) Holiday.getInstance().getDisguiseHandler()).getUsedNames().add(packet.getName().toLowerCase());
                    break;
                }
                case REMOVE:
                    ((DisguiseHandler_1_8) Holiday.getInstance().getDisguiseHandler()).getUsedNames().remove(packet.getName().toLowerCase());
            }
        }
    }
}
