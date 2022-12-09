package me.andyreckt.holiday.core.util.redis.pubsub.subscribers;

import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.rank.RankManager;
import me.andyreckt.holiday.core.util.redis.annotations.RedisListener;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.RankUpdatePacket;

public class RankUpdateSubscriber {
    @RedisListener
    public void onReceive(RankUpdatePacket packet) {
        RankManager rankManager = HolidayAPI.getUnsafeAPI().getRankManager();
        rankManager.getRanks().removeIf(rank -> rank.getName().equals(packet.getRank().getName()));
        if (packet.isDelete()) return;

        rankManager.getRanks().add(packet.getRank());
    }
}
