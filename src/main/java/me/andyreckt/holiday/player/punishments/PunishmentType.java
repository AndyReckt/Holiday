package me.andyreckt.holiday.player.punishments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;

@Getter
@AllArgsConstructor
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

    public String getMessage(PunishmentType type) {
        String string = null;
        switch (type) {
            case BAN: string = "Banned";
            case KICK: string = "Kicked";
            case MUTE: string = "Muted";
            case WARN: string = "Warned";
            case UNBAN: string = "Unbanned";
            case IP_BAN: string = "IP Banned";
            case UNMUTE: string = "Unmuted";
            case UNWARN: string = "Removed the warn of";
            case TEMP_BAN: string = "Temporarly Banned";
            case TEMP_MUTE: string = "Temporarly Muted";
            case BLACKLIST: string = "Blacklisted";
            case UNBLACKLIST: string = "Unblacklisted";
            default: string = null;
        }
        return string;
    }

    public static PunishmentType getByName(String name) {
        return Arrays.stream(values()).filter(type -> type.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}