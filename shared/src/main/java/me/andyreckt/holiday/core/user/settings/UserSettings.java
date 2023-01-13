package me.andyreckt.holiday.core.user.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.andyreckt.holiday.api.user.ISettings;

@Getter @Setter
@NoArgsConstructor
public class UserSettings implements ISettings {

    private boolean privateMessages = true;
    private boolean privateMessagesSounds = true;

}
