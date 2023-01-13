package me.andyreckt.holiday.bukkit.server.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@AllArgsConstructor
public class HelpopPacket implements Packet {

    private String sender;
    private String request;
    private String server;

}