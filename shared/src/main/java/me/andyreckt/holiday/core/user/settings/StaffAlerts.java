package me.andyreckt.holiday.core.user.settings;

import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IStaffAlerts;

@Getter @Setter
public class StaffAlerts implements IStaffAlerts {
    private boolean reportAlerts = false;
    private boolean requestAlerts = false;
} //TODO: All needed alerts & a menu to toggle them
