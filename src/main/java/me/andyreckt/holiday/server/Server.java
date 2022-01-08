package me.andyreckt.holiday.server;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.player.rank.Rank;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Server {
    String name;
    int players, maxplayers;
    boolean whitelisted;
    Rank whitelistRank;
    List<UUID> whitelistedPlayers;
}
