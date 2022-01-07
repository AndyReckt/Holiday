package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.database.redis.packet.ServerPacket;
import me.andyreckt.holiday.server.Server;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;

public class ServerListener implements PacketListener {

    @IncomingPacketHandler
    public void server(ServerPacket packet) {
        switch (packet.getType()) {
            case ADD: {
                if(!Server.servers.containsKey(packet.getData().getName())) Server.servers.put(packet.getData().getName(), packet.getData());
                break;
            }
            case KEEPALIVE: {
                Server.servers.remove(packet.getData().getName());
                Server.servers.put(packet.getData().getName(), packet.getData());
                break;
            }
            case REMOVE: {
                Server.servers.remove(packet.getData().getName());
                break;
            }
        }
    }


}
