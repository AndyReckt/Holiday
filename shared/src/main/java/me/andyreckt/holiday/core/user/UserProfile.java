package me.andyreckt.holiday.core.user;


import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.*;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.disguise.Disguise;
import me.andyreckt.holiday.core.user.settings.StaffSettings;
import me.andyreckt.holiday.core.user.settings.UserSettings;
import me.andyreckt.holiday.core.util.enums.ChatChannel;
import me.andyreckt.holiday.core.util.text.HashUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
public class UserProfile implements Profile {

    private final UUID uuid;

    private String name = "$undefined";

    private String ip = "";
    private List<String> ips = new ArrayList<>();
    private HashSet<UUID> alts = new HashSet<>();

    private List<String> permissions = new ArrayList<>();

    private boolean liked = false;

    private UserSettings settings = new UserSettings();
    private StaffSettings staffSettings = new StaffSettings();

    private final long firstLogin = System.currentTimeMillis();
    private long lastSeen = System.currentTimeMillis();

    private int coins = 0;
    private int credits = 0;

    private ChatChannel chatChannel = ChatChannel.GLOBAL;

    private Disguise disguise = null;

    public UserProfile(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getDisplayName() {
        if (isDisguised()) {
            return disguise.getDisplayName();
        }
        return getName();
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
        if (permissions.contains("-" + permission)) {
            return false;
        }
        if (this.permissions.contains(permission)) {
            return true;
        }
        for (IRank rank : getRanks()) {
            if (rank.getPermissions().contains("*")) {
                return true;
            }

            if (rank.getPermissions().contains("-" + permission)) {
                return false;
            }

            if (rank.getPermissions().contains(permission)) {
                return true;
            }

            for (UUID child : rank.getChilds()) {
                IRank childRank = HolidayAPI.getUnsafeAPI().getRank(child);
                if (childRank.getPermissions().contains("*")) {
                    return true;
                }

                if (childRank.getPermissions().contains("-" + permission)) {
                    return false;
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
        if (isDisguised()) {
            return disguise.getDisguiseRank();
        }

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
    public List<String> getAltsFormatted() {
        List<String> toReturn = new ArrayList<>();
        getAlts().forEach(uuid -> {
            Profile profile = HolidayAPI.getUnsafeAPI().getProfile(uuid);
            if (profile.isBlacklisted()) {
                toReturn.add("&4" + profile.getName());
            } else if (profile.isBanned() || profile.isIpBanned()) {
                toReturn.add("&c" + profile.getName());
            } else if (profile.isMuted()) {
                toReturn.add("&e&o" + profile.getName());
            } else if (profile.isOnline()) {
                toReturn.add("&a" + profile.getName());
            } else {
                toReturn.add("&7" + profile.getName());
            }
        });
        return toReturn;
    }

    @Override
    public void setDisguise(IDisguise disguise) {
        this.disguise = (Disguise) disguise;
    }

    @Override
    public boolean isDisguised() {
        return disguise != null;
    }

    public static UserProfile getConsoleProfile() {
        UserProfile console = new UserProfile(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        console.setName("Console");
        console.setPermissions(Collections.singletonList("*"));
        return console;
    }

}
//TODO: Implements the unimplemented methods.