package me.andyreckt.holiday.database.redis.subscibers;

import me.andyreckt.holiday.database.redis.packets.RankCreatePacket;
import me.andyreckt.holiday.database.redis.packets.RankDeletePacket;
import me.andyreckt.holiday.database.redis.packets.RankUpdatePacket;
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
