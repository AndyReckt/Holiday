package me.andyreckt.holiday.core.user.settings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IStaffSettings;

@Getter @Setter
@NoArgsConstructor
public class StaffSettings implements IStaffSettings {

    private boolean socialSpy = false;
    private boolean vanished = false;
    private boolean staffMode = false;

    private boolean staffChat = false;
    private boolean adminChat = false;

    private StaffAlerts alerts = new StaffAlerts();

}
