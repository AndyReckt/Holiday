package me.andyreckt.holiday.core.util.redis.pubsub.packets;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@RequiredArgsConstructor
public class ProfileUpdatePacket implements Packet {
    private final UserProfile profile;
}
