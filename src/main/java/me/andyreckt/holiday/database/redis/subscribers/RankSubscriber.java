package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.PermissionChangePacket;
import me.andyreckt.holiday.database.redis.packet.RankPacket;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.grant.Grant;
import me.andyreckt.holiday.utils.PermissionUtils;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;

public class RankSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void handle(RankPacket packet) {
        switch (packet.getUpdateType()) {
            case ADD:
            case UPDATE: {
                Holiday.getInstance().getRankHandler().updateCache(packet.getRank().getUuid(), packet.getRank());
                break;
            } case DELETE: {
                Holiday.getInstance().getRankHandler().removeFromCache(packet.getRank().getUuid());
                break;
            }
        }
    }

    @IncomingPacketHandler
    public void onPermChange(PermissionChangePacket packet) {
        for (Profile profile : Holiday.getInstance().getProfileHandler().getOnlineProfiles()) {
            for (Grant o : profile.getActiveGrants()) {
                if (o.getRank() == Holiday.getInstance().getRankHandler().getFromName(packet.getRank().getName())) {
                    PermissionUtils.updatePermissions(profile.getPlayer());
                }
            }
        }
    }

}
