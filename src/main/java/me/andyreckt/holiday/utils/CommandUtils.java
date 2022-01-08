package me.andyreckt.holiday.utils;

import me.andyreckt.holiday.player.Profile;

public class CommandUtils {

    public static boolean canBan(Profile issuer, Profile victim) {
        return issuer.getHighestGrant().getPriority() > victim.getHighestGrant().getPriority();
    }

}
