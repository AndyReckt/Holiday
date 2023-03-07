package me.andyreckt.holiday.api.server;

import me.andyreckt.holiday.api.user.IRank;

import java.util.List;
import java.util.UUID;

public interface IServer {

    String getServerName();
    String getServerId();

    double[] getTps();

    boolean isOnline();
    boolean isJoinable();

    String getAddress();
    int getPort();

    int getPlayerCount();
    List<UUID> getOnlinePlayers();
    int getMaxPlayers();
    void setOnlinePlayers(List<UUID> onlinePlayers);

    boolean isWhitelisted();
    void setWhitelisted(boolean whitelisted);

    IRank getWhitelistRank();
    void setWhitelistRank(IRank whitelistRank);

    List<UUID> getWhitelistedPlayers();
    void setWhitelistedPlayers(List<UUID> whitelistedPlayers);

    boolean isChatMuted();
    void setChatMuted(boolean chatMuted);

    long getChatDelay();
    void setChatDelay(long chatDelay);

    long getLastKeepAlive();
    void keepAlive();
    void sendUpdate();

    long getStartupTime();
    long getUptime();

    int getMemoryUsage();
    int getMemoryMax();
    int getMemoryFree();


}
