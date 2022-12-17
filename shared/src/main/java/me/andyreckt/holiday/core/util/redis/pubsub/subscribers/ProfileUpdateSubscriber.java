package me.andyreckt.holiday.core.util.redis.pubsub.subscribers;

import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.UserManager;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.ProfileUpdatePacket;

public class ProfileUpdateSubscriber implements PacketListener {
    @IncomingPacketHandler
    public void onReceive(ProfileUpdatePacket packet) {
        UserManager userManager = HolidayAPI.getUnsafeAPI().getUserManager();

        userManager.getProfiles().put(packet.getProfile().getUuid(), packet.getProfile());
    }
}
