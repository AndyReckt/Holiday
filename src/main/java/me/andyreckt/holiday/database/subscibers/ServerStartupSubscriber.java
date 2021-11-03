package me.andyreckt.holiday.database.subscibers;

import me.andyreckt.holiday.Files;
import me.andyreckt.holiday.database.packets.ServerStartPacket;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;

public class ServerStartupSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void start(ServerStartPacket packet) {
        CC.sendMessageToAdmins(Files.Messages.START_MESSAGE.getString().replace("<server>", packet.getServerName()));
    }

}
