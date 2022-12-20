package me.andyreckt.holiday.core.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.core.HolidayAPI;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public enum AlertType {
    REPORT("Report Alerts"),
    REQUEST("Request Alerts"),
    SILENT_PUNISHMENT("Silent Punishment Alerts"),
    CHAT("Chat Alerts"),
    GAMEMODE("Gamemode Alerts"),
    TELEPORT("Teleport Alerts"),
    WHITELIST("Whitelist Alerts"),
    ALT_LOGIN("Alt Login Alerts"),

    SERVER("Server Alerts", true),
    SERVER_MANAGER("Server Manager Alerts", true),
    BANNED_LOGIN("Banned Login Alerts", true),

    ;

    private final String name;
    private final boolean admin;

    AlertType(String name) {
        this.name = name;
        this.admin = false;
    }


    public boolean isAlerts(Profile profile) {
        switch (this) {
            case REPORT:
                return profile.getStaffSettings().getAlerts().isReportAlerts();
            case REQUEST:
                return profile.getStaffSettings().getAlerts().isRequestAlerts();
            case CHAT:
                return profile.getStaffSettings().getAlerts().isChatAlerts();
            case GAMEMODE:
                return profile.getStaffSettings().getAlerts().isGamemodeAlerts();
            case TELEPORT:
                return profile.getStaffSettings().getAlerts().isTeleportAlerts();
            case WHITELIST:
                return profile.getStaffSettings().getAlerts().isWhitelistAlerts();
            case SERVER:
                return profile.getStaffSettings().getAlerts().isServerAlerts();
            case SERVER_MANAGER:
                return profile.getStaffSettings().getAlerts().isServerManagerAlerts();
            case BANNED_LOGIN:
                return profile.getStaffSettings().getAlerts().isBannedLoginAlerts();
            case ALT_LOGIN:
                return profile.getStaffSettings().getAlerts().isAltLoginAlerts();
            case SILENT_PUNISHMENT:
                return profile.getStaffSettings().getAlerts().isSilentPunishmentAlerts();
        }

        return false;
    }

    public void setAlerts(Profile profile, boolean alerts) {
        switch (this) {
            case REPORT:
                profile.getStaffSettings().getAlerts().setReportAlerts(alerts);
                HolidayAPI.getUnsafeAPI().saveProfile(profile);
                break;
            case REQUEST:
                profile.getStaffSettings().getAlerts().setRequestAlerts(alerts);
                HolidayAPI.getUnsafeAPI().saveProfile(profile);
                break;
            case CHAT:
                profile.getStaffSettings().getAlerts().setChatAlerts(alerts);
                HolidayAPI.getUnsafeAPI().saveProfile(profile);
                break;
            case GAMEMODE:
                profile.getStaffSettings().getAlerts().setGamemodeAlerts(alerts);
                HolidayAPI.getUnsafeAPI().saveProfile(profile);
                break;
            case TELEPORT:
                profile.getStaffSettings().getAlerts().setTeleportAlerts(alerts);
                HolidayAPI.getUnsafeAPI().saveProfile(profile);
                break;
            case WHITELIST:
                profile.getStaffSettings().getAlerts().setWhitelistAlerts(alerts);
                HolidayAPI.getUnsafeAPI().saveProfile(profile);
                break;
            case SERVER:
                profile.getStaffSettings().getAlerts().setServerAlerts(alerts);
                HolidayAPI.getUnsafeAPI().saveProfile(profile);
                break;
            case SERVER_MANAGER:
                profile.getStaffSettings().getAlerts().setServerManagerAlerts(alerts);
                HolidayAPI.getUnsafeAPI().saveProfile(profile);
                break;
            case BANNED_LOGIN:
                profile.getStaffSettings().getAlerts().setBannedLoginAlerts(alerts);
                HolidayAPI.getUnsafeAPI().saveProfile(profile);
                break;
            case ALT_LOGIN:
                profile.getStaffSettings().getAlerts().setAltLoginAlerts(alerts);
                HolidayAPI.getUnsafeAPI().saveProfile(profile);
                break;
            case SILENT_PUNISHMENT:
                profile.getStaffSettings().getAlerts().setSilentPunishmentAlerts(alerts);
                HolidayAPI.getUnsafeAPI().saveProfile(profile);
                break;
        }
    }

    public boolean isAlerts(UUID uuid) {
        return isAlerts(HolidayAPI.getUnsafeAPI().getProfile(uuid));
    }
}
