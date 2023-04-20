package me.andyreckt.holiday.core.server;

import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.server.IServer;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.rank.Rank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class Server implements IServer {

    private final String serverName;
    private final String serverId;
    private final long startupTime;

    private final String address;
    private final int port;

    private double[] tps = new double[3];

    private UUID whitelistRank = HolidayAPI.getUnsafeAPI().getDefaultRank().getUuid();

    private int maxPlayers = 250;
    private int memoryMax = 0;
    private int memoryFree = 0;

    private boolean whitelisted = false;
    private boolean chatMuted = false;
    private boolean joinable = false;

    private long chatDelay = 0L;
    private long lastKeepAlive = System.currentTimeMillis();

    private List<UUID> onlinePlayers = new ArrayList<>();
    private List<UUID> whitelistedPlayers = new ArrayList<>();

    public Server(String serverName, String serverId, String address, int port) {
        this.serverName = serverName;
        this.serverId = serverId;
        this.address = address;
        this.port = port;
        this.startupTime = System.currentTimeMillis();
    }

    @Override
    public boolean isOnline() {
        return System.currentTimeMillis() - lastKeepAlive < 10_000L;
    }

    @Override
    public int getPlayerCount() {
        return getOnlinePlayers().size();
    }

    @Override
    public void setWhitelistRank(IRank whitelistRank) {
        whitelistRank = (whitelistRank == null || whitelistRank.getUuid() == null) ? HolidayAPI.getUnsafeAPI().getDefaultRank() : whitelistRank;
        this.whitelistRank = whitelistRank.getUuid();
    }

    @Override
    public void keepAlive() {
        this.lastKeepAlive = System.currentTimeMillis();
        HolidayAPI.getUnsafeAPI().getServerManager().keepAlive(this);
    }

    @Override
    public void sendUpdate() {
        HolidayAPI.getUnsafeAPI().getServerManager().sendUpdate(this);
    }

    @Override
    public long getUptime() {
        return System.currentTimeMillis() - startupTime;
    }

    @Override
    public int getMemoryUsage() {
        return memoryMax - memoryFree;
    }

    @Override
    public IRank getWhitelistRank() {
        IRank whitelistRank = HolidayAPI.getUnsafeAPI().getRank(this.whitelistRank);

        if (whitelistRank == null) return HolidayAPI.getUnsafeAPI().getDefaultRank();

        return whitelistRank;
    }

}
