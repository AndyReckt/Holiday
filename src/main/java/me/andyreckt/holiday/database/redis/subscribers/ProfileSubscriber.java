package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.ProfilePacket;
import me.andyreckt.holiday.database.redis.packet.StaffSwitchServer;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;
import org.bukkit.Bukkit;

public class ProfileSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onChange(ProfilePacket packet) {
        ProfileHandler ph = Holiday.getInstance().getProfileHandler();
        if (ph.isCached(packet.getProfile().getUuid())) ph.updateProfile(packet.getProfile());
        packet.getProfile().setPlayerListName();
        packet.getProfile().setBukkitDisplayName();
    }

    @IncomingPacketHandler
    public void onDelete(ProfilePacket.ProfileDeletePacket packet) {
        Holiday.getInstance().getProfileHandler().removeFromCache(packet.getProfile().getUuid());
    }

    @IncomingPacketHandler
    public void onMessage(ProfilePacket.ProfileMessagePacket packet) {
        if (Bukkit.getPlayer(packet.getProfile().getUuid()) != null) packet.getProfile().getPlayer().sendMessage(CC.translate(packet.getMessage()));
        packet.getProfile().setPlayerListName();
        packet.getProfile().setBukkitDisplayName();
    }



    @IncomingPacketHandler
    public void onStaffChange(StaffSwitchServer.StaffPacket packet) {
        if (packet.isLeft()) {
            StaffSwitchServer.staffChange.remove(packet.getProfile().getUuid());
            StaffSwitchServer.staffChange.remove(packet.getProfile().getUuid());
            StaffSwitchServer.staffChange.put(packet.getProfile().getUuid(), new StaffSwitchServer.StaffSwitchData(System.currentTimeMillis(), packet.getProfile().getCurrentServer()));
        }
    }
}
