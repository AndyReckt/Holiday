package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.ProfilePacket;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;

public class ProfileSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onChange(ProfilePacket packet) {
        ProfileHandler ph = Holiday.getInstance().getProfileHandler();
        if (ph.isCached(packet.getProfile().getUuid())) ph.updateProfile(packet.getProfile());
    }
}
