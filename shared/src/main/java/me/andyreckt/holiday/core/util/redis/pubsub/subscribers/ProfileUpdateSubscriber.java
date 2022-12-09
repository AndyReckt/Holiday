package me.andyreckt.holiday.core.util.redis.pubsub.subscribers;

import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.UserManager;
import me.andyreckt.holiday.core.util.redis.annotations.RedisListener;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.ProfileUpdatePacket;

public class ProfileUpdateSubscriber {
    @RedisListener
    public void onReceive(ProfileUpdatePacket packet) {
        UserManager userManager = HolidayAPI.getUnsafeAPI().getUserManager();

        userManager.getProfiles().put(packet.getProfile().getUuid(), packet.getProfile());
    }
}
