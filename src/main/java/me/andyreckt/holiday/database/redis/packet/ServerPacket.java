package me.andyreckt.holiday.database.redis.packet;

import me.andyreckt.holiday.other.enums.ServerPacketType;
import me.andyreckt.holiday.server.Server;
import me.andyreckt.holiday.utils.packets.Packet;

public class ServerPacket implements Packet {
    final Server.ServerData data;

    final ServerPacketType type;

    public ServerPacket(Server.ServerData data, ServerPacketType type) {
        this.data = data;
        this.type = type;
    }

    public Server.ServerData getData() {
        return this.data;
    }

    public ServerPacketType getType() {
        return this.type;
    }
}
