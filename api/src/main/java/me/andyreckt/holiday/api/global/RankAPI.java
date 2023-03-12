package me.andyreckt.holiday.api.global;

import me.andyreckt.holiday.api.user.IRank;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RankAPI {
    IRank getDefaultRank();

    Set<IRank> getRanks();
    List<IRank> getRanksSorted();

    IRank createRank(String name);

    void saveRank(IRank rank);
    void deleteRank(IRank rank);

    IRank getRank(String name);
    IRank getRank(UUID uuid);
}
