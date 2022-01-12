package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.RankPacket;
import me.andyreckt.holiday.other.enums.RankType;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;

public class RankSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void handle(RankPacket packet) {
        switch (packet.getUpdateType()) {
            case ADD:
            case UPDATE: {
                Holiday.getInstance().getRankHandler().updateCache(packet.getRank().getUuid(), packet.getRank());
                break;
            } case DELETE: {
                Holiday.getInstance().getRankHandler().removeFromCache(packet.getRank().getUuid());
                break;
            }
        }
    }


}
