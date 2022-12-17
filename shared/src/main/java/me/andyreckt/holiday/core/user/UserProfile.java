package me.andyreckt.holiday.core.user;


import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IGrant;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.text.HashUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter @Setter
public class UserProfile implements Profile {

    private final UUID uuid;

    private String name = "$undefined";

    private String ip = "";
    private List<String> ips = new ArrayList<>();

    private List<String> permissions = new ArrayList<>();

    private boolean liked = false;

    private final long firstLogin = System.currentTimeMillis();
    private long lastSeen = System.currentTimeMillis();

    private int coins = 0;
    private int credits = 0;

    public UserProfile(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getLowercaseName() {
        return getName().toLowerCase();
    }

    @Override
    public void addNewCurrentIP(String ip) {
        String hash = HashUtils.hash(ip);
        if (!ips.contains(hash)) {
            ips.add(hash);
        }
        this.ip = hash;
    }

    @Override
    public void addPermission(String permission) {
        getPermissions().add(permission);
    }

    @Override
    public void removePermission(String permission) {
        getPermissions().remove(permission);
    }

    @Override
    public boolean hasPermission(String permission) {
        if (this.permissions.contains("*")) {
            return true;
        }
        if (this.permissions.contains(permission)) {
            return true;
        }
        for (IRank rank : getRanks()) {
            if (rank.getPermissions().contains("*")) {
                return true;
            }
            if (rank.getPermissions().contains(permission)) {
                return true;
            }

            for (UUID child : rank.getChilds()) {
                IRank childRank = HolidayAPI.getUnsafeAPI().getRank(child);
                if (childRank.getPermissions().contains("*")) {
                    return true;
                }
                if (childRank.getPermissions().contains(permission)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<IGrant> getGrants() {
        return HolidayAPI.getUnsafeAPI().getGrants(uuid);
    }

    @Override
    public List<IGrant> getActiveGrants() {
        return getGrants().stream().filter(IGrant::isActive).collect(Collectors.toList());
    }

    @Override
    public IGrant getHighestGrant() {
        return getActiveGrants().stream().max(Comparator.comparingInt(grant -> grant.getRank().getPriority())).orElse(null);
    }

    @Override
    public IGrant getHighestVisibleGrant() {
        return getActiveGrants().stream().filter(grant -> grant.getRank().isVisible()).max(Comparator.comparingInt(grant -> grant.getRank().getPriority())).orElse(null);
    }

    @Override
    public List<IRank> getRanks() {
        List<IRank> ranks = getActiveGrants().stream().map(IGrant::getRank).collect(Collectors.toList());
        ranks.add(HolidayAPI.getUnsafeAPI().getDefaultRank());
        return ranks;
    }

    @Override
    public IRank getHighestRank() {
        return getRanks().stream().max(Comparator.comparingInt(IRank::getPriority)).orElse(null);
    }

    @Override
    public IRank getHighestVisibleRank() {
        List<IRank> ranks = getActiveGrants().stream().map(IGrant::getRank).filter(IRank::isVisible).collect(Collectors.toList());
        ranks.add(HolidayAPI.getUnsafeAPI().getDefaultRank());
        return ranks.stream().max(Comparator.comparingInt(IRank::getPriority)).orElse(null);
    }

    @Override
    public IRank getDisplayRank() {
        //TODO: Add support for disguises
        return getHighestVisibleRank();
    }

    @Override
    public boolean isStaff() {
        return getActiveGrants().stream().anyMatch(grant -> grant.getRank().isStaff());
    }

    @Override
    public boolean isAdmin() {
        return getActiveGrants().stream().anyMatch(grant -> grant.getRank().isAdmin());
    }

    @Override
    public boolean isOp() {
        return getActiveGrants().stream().anyMatch(grant -> grant.getRank().isOp());
    }

    @Override
    public boolean isOnline() {
        return HolidayAPI.getUnsafeAPI().isOnline(uuid);
    }

    @Override
    public boolean isMuted() {
        return HolidayAPI.getUnsafeAPI().getPunishments(uuid).stream().filter(IPunishment::isActive).anyMatch(type -> type.getType() == IPunishment.PunishmentType.MUTE);
    }

    @Override
    public boolean isBanned() {
        return HolidayAPI.getUnsafeAPI().getPunishments(uuid).stream().filter(IPunishment::isActive).anyMatch(type -> type.getType() == IPunishment.PunishmentType.BAN);
    }

    @Override
    public boolean isIpBanned() {
        return HolidayAPI.getUnsafeAPI().getPunishments(uuid).stream().filter(IPunishment::isActive).anyMatch(type -> type.getType() == IPunishment.PunishmentType.IP_BAN);
    }

    @Override
    public boolean isBlacklisted() {
        return HolidayAPI.getUnsafeAPI().getPunishments(uuid).stream().filter(IPunishment::isActive).anyMatch(type -> type.getType() == IPunishment.PunishmentType.BLACKLIST);
    }

    @Override
    public List<IPunishment> getPunishments() {
        return HolidayAPI.getUnsafeAPI().getPunishments(uuid);
    }

    @Override
    public List<IPunishment> getPunishments(IPunishment.PunishmentType type) {
        return HolidayAPI.getUnsafeAPI().getPunishments(uuid).stream().filter(punishment -> punishment.getType() == type).collect(Collectors.toList());
    }

    @Override
    public List<IPunishment> getActivePunishments() {
        return HolidayAPI.getUnsafeAPI().getPunishments(uuid).stream().filter(IPunishment::isActive).collect(Collectors.toList());
    }

    @Override
    public List<IPunishment> getActivePunishments(IPunishment.PunishmentType type) {
        return HolidayAPI.getUnsafeAPI().getPunishments(uuid).stream().filter(punishment -> punishment.getType() == type).filter(IPunishment::isActive).collect(Collectors.toList());
    }

    @Override
    public List<Profile> getAlts() {
        List<Profile> toReturn = new ArrayList<>();
        List<String> ips = getIps();
        HolidayAPI.getUnsafeAPI().getUserManager().getProfiles().forEach((uuid, profile) -> {
            if (profile.getIps().stream().anyMatch(ips::contains)) {
                toReturn.add(profile);
            }
        });
        return toReturn;
    }

    @Override
    public List<String> formatAlts() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public static UserProfile getConsoleProfile() {
        UserProfile console = new UserProfile(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        console.setName("Console");
        return console;
    }

}
//TODO: Implements the unimplemented methods.