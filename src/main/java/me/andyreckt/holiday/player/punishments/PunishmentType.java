package me.andyreckt.holiday.player.punishments;


import lombok.NoArgsConstructor;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;


@NoArgsConstructor
public enum PunishmentType {

    BLACKLIST(),
    UNBLACKLIST(),
    IP_BAN(),
    UNIP_BAN(),
    BAN(),
    TEMP_BAN(),
    UNBAN(),
    MUTE(),
    TEMP_MUTE(),
    UNMUTE(),
    WARN(),
    UNWARN(),
    KICK();


    public String getName() {
        if(this.name().equals("IP_BAN")) {
            return "IP Ban";
        }

        return WordUtils.capitalizeFully(this.name());
    }

    public static PunishmentType getByName(String name) {
        return Arrays.stream(values()).filter(type -> type.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}