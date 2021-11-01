package me.andyreckt.holiday.database.subscibers;

import me.andyreckt.holiday.database.packets.ServerStartPacket;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;

public class ServerStartupSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void start(ServerStartPacket packet) {

    }

}
