package me.andyreckt.holiday.api.user;

import java.util.List;
import java.util.UUID;

public interface Profile {

    UUID getUuid();

    String getName();
    void setName(String name);
    String getLowercaseName();

    String getIp();
    List<String> getIps();
    void addNewCurrentIP(String ip);

    void addPermission(String permission);
    void removePermission(String permission);
    List<String> getPermissions();

    boolean isLiked();
    void setLiked(boolean liked);

    long getFirstLogin();
    long getLastSeen();
    void setLastSeen(long lastSeen);

    int getCoins();
    void setCoins(int coins);
    int getCredits();
    void setCredits(int credits);

    List<IGrant> getGrants();
    List<IGrant> getActiveGrants();
    IGrant getHighestGrant();
    IGrant getHighestVisibleGrant();

    List<IRank> getRanks();
    IRank getHighestRank();
    IRank getHighestVisibleRank();
    IRank getDisplayRank();

    boolean isStaff();
    boolean isAdmin();
    boolean isOp();

    boolean isOnline();

    boolean isMuted();
    boolean isBanned();
    boolean isIpBanned();
    boolean isBlacklisted();

    List<IPunishment> getPunishments();
    List<IPunishment> getPunishments(IPunishment.PunishmentType type);
    List<IPunishment> getActivePunishments();
    List<IPunishment> getActivePunishments(IPunishment.PunishmentType type);


    //TODO: Add disguise-related methods
    //TODO: Add settings-related methods
    //TODO: Add staff-related methods

    List<Profile> getAlts();
    List<String> formatAlts();


}
