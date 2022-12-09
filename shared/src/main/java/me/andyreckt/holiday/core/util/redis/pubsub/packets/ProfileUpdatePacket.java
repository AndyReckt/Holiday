package me.andyreckt.holiday.core.util.redis.pubsub.packets;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.util.redis.annotations.RedisObject;

@Getter
@RequiredArgsConstructor
@RedisObject(id = "ProfileUpdatePacket")
public class ProfileUpdatePacket {
    private final UserProfile profile;
}
