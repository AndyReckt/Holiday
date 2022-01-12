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
    private String name;
    private int players, maxplayers;
    private boolean whitelisted;
    private Rank whitelistRank;
    private List<UUID> whitelistedPlayers;

    private long chatDelay;
    private boolean chatMuted;
}
