package me.andyreckt.holiday.core.util.enums;

import lombok.Getter;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.core.HolidayAPI;

import java.util.UUID;

@Getter
public enum AlertType {
    REPORT("Report Alerts"),
    REQUEST("Request Alerts")
    ;

    private final String name;

    AlertType(String name) {
        this.name = name;
    }


    public boolean isAlerts(Profile profile) {
        switch (this) {
            case REPORT:
                return profile.getStaffSettings().getAlerts().isReportAlerts();
            case REQUEST:
                return profile.getStaffSettings().getAlerts().isRequestAlerts();
        }

        return false;
    }

    public boolean isAlerts(UUID uuid) {
        return isAlerts(HolidayAPI.getUnsafeAPI().getProfile(uuid));
    }
}
