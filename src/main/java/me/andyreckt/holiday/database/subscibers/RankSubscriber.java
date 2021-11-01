package me.andyreckt.holiday.database.subscibers;

import me.andyreckt.holiday.database.packets.RankCreatePacket;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;

public class RankSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void start(RankCreatePacket packet) {

    }

}
