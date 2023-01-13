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
    STAFF_CHAT("Staff Chat"),
    ADMIN_CHAT("Admin Chat"),
    DISGUISES("Whitelist Alerts"),
    ALT_LOGIN("Alt Login Alerts"),
    ABUSE("Abuse Alerts"),

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
            case STAFF_CHAT:
                return profile.getStaffSettings().getAlerts().isStaffChat();
            case ADMIN_CHAT:
                return profile.getStaffSettings().getAlerts().isAdminChat();
            case ABUSE:
                return profile.getStaffSettings().getAlerts().isDisguiseAlerts();
            case DISGUISES:
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
            case STAFF_CHAT:
                profile.getStaffSettings().getAlerts().setStaffChat(alerts);
                HolidayAPI.getUnsafeAPI().saveProfile(profile);
                break;
            case ADMIN_CHAT:
                profile.getStaffSettings().getAlerts().setAdminChat(alerts);
                HolidayAPI.getUnsafeAPI().saveProfile(profile);
                break;
            case ABUSE:
                profile.getStaffSettings().getAlerts().setDisguiseAlerts(alerts);
                HolidayAPI.getUnsafeAPI().saveProfile(profile);
                break;
            case DISGUISES:
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
