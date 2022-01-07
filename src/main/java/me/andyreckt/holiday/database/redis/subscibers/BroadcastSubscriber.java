package me.andyreckt.holiday.database.redis.subscibers;

import me.andyreckt.holiday.database.redis.packets.BroadcastPacket;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;
import org.bukkit.Bukkit;

public class BroadcastSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onBroadcast(BroadcastPacket packet) {
        switch (packet.getType()){
            case ALL: {
                Bukkit.broadcastMessage(packet.getMessage());
            }
            case DEV: {
                Profile.getAllProfiles().forEach(profile -> {
                    if(profile.getHighestRank().isDev()) profile.getPlayer().sendMessage(CC.translate(packet.getMessage()));
                });
            }
            case ADMIN: {
                Profile.getAllProfiles().forEach(profile -> {
                    if(profile.getHighestRank().isAdmin()) profile.getPlayer().sendMessage(CC.translate(packet.getMessage()));
                });
            }
            case STAFF: {
                Profile.getAllProfiles().forEach(profile -> {
                    if(profile.getHighestRank().isStaff()) profile.getPlayer().sendMessage(CC.translate(packet.getMessage()));
                });
            }
        }
    }
}
