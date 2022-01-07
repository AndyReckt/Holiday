package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.database.redis.packet.RankCreatePacket;
import me.andyreckt.holiday.database.redis.packet.RankDeletePacket;
import me.andyreckt.holiday.database.redis.packet.RankUpdatePacket;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;

public class RankSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void create(RankCreatePacket packet) {

    }

    @IncomingPacketHandler
    public void delete(RankDeletePacket packet) {

    }

    @IncomingPacketHandler
    public void update(RankUpdatePacket packet) {

    }

}
