package me.andyreckt.holiday.core.util.redis.pubsub.packets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.user.rank.Rank;
import me.andyreckt.holiday.core.util.redis.annotations.RedisObject;

@Getter
@RequiredArgsConstructor
@RedisObject(id = "RankUpdatePacket")
public class RankUpdatePacket {
    private final Rank rank;
    private final boolean delete;

    public RankUpdatePacket(Rank rank) {
        this.rank = rank;
        this.delete = false;
    }
}
