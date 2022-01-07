package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.database.redis.packet.BroadcastPacket;
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
                Bukkit.broadcastMessage(CC.translate(packet.getMessage()));
                break;
            }
            case STAFF: {
                Profile.getAllProfiles().forEach(profile -> {
                    if(profile.getRank().isStaff() && profile.getPlayer() != null) profile.getPlayer().sendMessage(CC.translate(packet.getMessage()));
                });
                break;
            }
            case ADMIN: {
                Profile.getAllProfiles().forEach(profile -> {
                    if(profile.getRank().isAdmin() && profile.getPlayer() != null) profile.getPlayer().sendMessage(CC.translate(packet.getMessage()));
                });
                break;
            }
            case OP: {
                Profile.getAllProfiles().forEach(profile -> {
                    if(profile.getRank().isOp() && profile.getPlayer() != null) profile.getPlayer().sendMessage(CC.translate(packet.getMessage()));
                });
                break;
            }
        }
    }
}
