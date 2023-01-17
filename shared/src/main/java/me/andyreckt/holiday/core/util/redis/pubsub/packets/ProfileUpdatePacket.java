package me.andyreckt.holiday.core.util.redis.pubsub.packets;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.UserManager;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@RequiredArgsConstructor
public class ProfileUpdatePacket implements Packet {
    private final UserProfile profile;

    @Override
    public void onReceive() {
        UserManager userManager = HolidayAPI.getUnsafeAPI().getUserManager();
        if (userManager.getProfiles().containsKey(profile.getUuid())) {
            userManager.getProfiles().put(profile.getUuid(), profile);
        }
    }
}
