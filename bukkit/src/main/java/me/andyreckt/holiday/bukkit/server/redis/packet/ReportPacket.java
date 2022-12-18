package me.andyreckt.holiday.bukkit.server.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@AllArgsConstructor
public class ReportPacket implements Packet {

    private String reporter;
    private String reported;
    private String reason;
    private String server;

}