package me.andyreckt.holiday.core.util.redis.pubsub.packets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.rank.Rank;
import me.andyreckt.holiday.core.user.rank.RankManager;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@RequiredArgsConstructor
public class RankUpdatePacket implements Packet {
    private final Rank rank;
    private final boolean delete;

    public RankUpdatePacket(Rank rank) {
        this.rank = rank;
        this.delete = false;
    }

    @Override
    public void onReceive() {
        RankManager rankManager = HolidayAPI.getUnsafeAPI().getRankManager();

        rankManager.getRanks().removeIf(rank -> rank.getUuid().equals(this.rank.getUuid()));
        if (delete) return;

        rankManager.getRanks().add(this.rank);
    }
}
