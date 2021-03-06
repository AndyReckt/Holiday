package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.GrantPacket;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;

public class GrantSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void update(GrantPacket packet) {
        switch (packet.getUpdateType()) {
            case UPDATE:
            case ADD: {
                Holiday.getInstance().getGrantHandler().updateCache(packet.getGrant().getUuid(), packet.getGrant());
                break;
            }
            case DELETE: {
                Holiday.getInstance().getGrantHandler().removeFromCache(packet.getGrant().getUuid());
            }
        }
    }

}
