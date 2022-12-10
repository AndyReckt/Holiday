package me.andyreckt.holiday.utils;

import me.andyreckt.holiday.player.Profile;

public class CommandUtils {

    public static boolean canPunish(Profile issuer, Profile victim) {
        if (issuer.isConsole()) return true;
        return issuer.getHighestGrant().getPriority() > victim.getHighestGrant().getPriority();
    }

}
