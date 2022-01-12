package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.ServerPacket;
import me.andyreckt.holiday.server.Server;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;

public class ServerSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void server(ServerPacket packet) {
        switch (packet.getType()) {
            case ADD: {
                if(!Holiday.getInstance().getServerHandler().getServers().containsKey(packet.getData().getName())) Holiday.getInstance().getServerHandler().getServers().put(packet.getData().getName(), packet.getData());
                break;
            }
            case KEEPALIVE: {
                Holiday.getInstance().getServerHandler().getServers().remove(packet.getData().getName());
                Holiday.getInstance().getServerHandler().getServers().put(packet.getData().getName(), packet.getData());
                break;
            }
            case REMOVE: {
                try {
                    Holiday.getInstance().getServerHandler().getServers().remove(packet.getData().getName());
                } catch (Exception ignored){}
                break;
            }
        }
    }




}
