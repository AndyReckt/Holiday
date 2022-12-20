package me.andyreckt.holiday.core.user.settings;

import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IStaffAlerts;

@Getter @Setter
public class StaffAlerts implements IStaffAlerts {
    private boolean reportAlerts = true;
    private boolean requestAlerts = true;
    private boolean chatAlerts = true;
    private boolean gamemodeAlerts = true;
    private boolean teleportAlerts = true;
    private boolean whitelistAlerts = true;
    private boolean serverAlerts = true;
    private boolean serverManagerAlerts = true;
    private boolean bannedLoginAlerts = true;
    private boolean altLoginAlerts = true;
    private boolean silentPunishmentAlerts = true;
} //TODO: All needed alerts & a menu to toggle them
