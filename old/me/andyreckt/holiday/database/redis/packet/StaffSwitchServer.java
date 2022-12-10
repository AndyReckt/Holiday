package me.andyreckt.holiday.database.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.other.enums.BroadcastType;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.StringUtil;
import me.andyreckt.holiday.utils.Tasks;
import me.andyreckt.holiday.utils.packets.Packet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class StaffSwitchServer {
    @Getter
    public static final Map<UUID, StaffSwitchData> staffChange = new HashMap<>(); //yes its static ok ik its bad

    public StaffSwitchServer(Profile p1, boolean leave) {

        if (leave) {
            Holiday.getInstance().getRedis().sendPacket(new StaffPacket(p1, true));
            Tasks.runAsyncLater(() -> {
                if (new Profile(p1.getUuid(), false).isOnline()) return;
                Holiday.getInstance().getRedis().sendPacket(new BroadcastPacket(
                        applyPlaceholders(Holiday.getInstance().getMessages().getString("STAFF.LEAVE"), p1.getNameWithColor()),
                        BroadcastType.STAFF
                ));
            }, 30L);
        } else {
            if (staffChange.containsKey(p1.getUuid())) {

                Long time = staffChange.get(p1.getUuid()).getL();
                Long now = System.currentTimeMillis();

                if ((now - time) < 1500) {
                    Holiday.getInstance().getRedis().sendPacket(new BroadcastPacket(
                            applyPlaceholders(Holiday.getInstance().getMessages().getString("STAFF.SWITCH"), p1.getNameWithColor(), staffChange.get(p1.getUuid()).getServer()),
                            BroadcastType.STAFF
                    ));
                    staffChange.remove(p1.getUuid());
                } else {
                    staffChange.remove(p1.getUuid());
                    Holiday.getInstance().getRedis().sendPacket(new BroadcastPacket(
                            applyPlaceholders(Holiday.getInstance().getMessages().getString("STAFF.JOIN"), p1.getNameWithColor()),
                            BroadcastType.STAFF
                    ));
                }
            } else {
                Holiday.getInstance().getRedis().sendPacket(new BroadcastPacket(
                        applyPlaceholders(Holiday.getInstance().getMessages().getString("STAFF.JOIN"), p1.getNameWithColor()),
                        BroadcastType.STAFF
                ));
            }
        }
    }

    String applyPlaceholders(String message, String player) {
        message = message.replace("<player>", player);
        message = message.replace("<server>", Holiday.getInstance().getSettings().getString("SERVER.NICENAME"));
        message = StringUtil.addNetworkPlaceholder(message);
        return message;
    }

    String applyPlaceholders(String message, String player, String oldServer) {
        message = message.replace("<player>", player);
        message = message.replace("<server>", Holiday.getInstance().getSettings().getString("SERVER.NICENAME"));
        message = message.replace("<oldserver>", oldServer);
        message = StringUtil.addNetworkPlaceholder(message);
        return message;
    }

    @AllArgsConstructor
    @Getter
    public static class StaffPacket implements Packet {
        Profile profile;
        boolean left;
    }

    @AllArgsConstructor
    @Getter
    public static class StaffSwitchData {
        Long l;
        String server;
    }
}
