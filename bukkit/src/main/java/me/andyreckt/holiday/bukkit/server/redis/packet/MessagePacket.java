package me.andyreckt.holiday.bukkit.server.redis.packet;

import me.andyreckt.holiday.core.user.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@AllArgsConstructor
public class MessagePacket implements Packet {

    private final UserProfile target, sender;
    private final String message;

}