package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.RankPacket;
import me.andyreckt.holiday.other.enums.RankType;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;

public class RankSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void create(RankPacket packet) {

        if (!(packet.getUpdateType() == RankType.ADD)) return;
        Holiday.getInstance().getRankHandler().updateCache(packet.getRank().getUuid(), packet.getRank());

    }

    @IncomingPacketHandler
    public void delete(RankPacket packet) {

        if (!(packet.getUpdateType() == RankType.DELETE)) return;
        Holiday.getInstance().getRankHandler().removeFromCache(packet.getRank().getUuid());

    }

    @IncomingPacketHandler
    public void update(RankPacket packet) {

        if (!(packet.getUpdateType() == RankType.UPDATE)) return;
        Holiday.getInstance().getRankHandler().updateCache(packet.getRank().getUuid(), packet.getRank());

    }

}
