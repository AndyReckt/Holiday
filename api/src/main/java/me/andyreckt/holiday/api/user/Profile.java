package me.andyreckt.holiday.api.user;

import com.google.gson.annotations.SerializedName;
import me.andyreckt.holiday.api.server.IServer;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("rawtypes")
public interface Profile {

    @SerializedName("_id")
    UUID getUuid();

    String getName();
    String getDisplayName();
    void setName(String name);
    String getLowercaseName();

    String getIp();
    List<String> getIps();
    void addNewCurrentIP(String ip);

    void addPermission(String permission);
    void removePermission(String permission);
    List<String> getPermissions();
    boolean hasPermission(String permission);

    boolean isLiked();
    void setLiked(boolean liked);

    ISettings getSettings();
    IStaffSettings getStaffSettings();

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
    boolean hasRank(IRank rank);
    boolean hasRank(String rankName);
    boolean hasRank(UUID rankId);

    boolean isStaff();
    boolean isAdmin();
    boolean isOp();

    boolean isOnline();

    boolean isMuted();
    boolean isBanned();
    boolean isIpBanned();
    boolean isBlacklisted();

    IServer getCurrentServer();

    List<IPunishment> getPunishments();
    List<IPunishment> getPunishments(IPunishment.PunishmentType type);
    List<IPunishment> getActivePunishments();
    List<IPunishment> getActivePunishments(IPunishment.PunishmentType type);

    Set<UUID> getAlts();
    List<String> getAltsFormatted();

    IDisguise getDisguise();
    void setDisguise(IDisguise disguise);
    boolean isDisguised();

    List<IMetadata> getMetadatas();
    IMetadata getMetadata(String id, IMetadata defaultValue);
    void setMetadata(IMetadata metadata);
    <T> void setMetadata(String id, T value);
}
