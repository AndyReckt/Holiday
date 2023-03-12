package me.andyreckt.holiday.bukkit.user;

import lombok.experimental.UtilityClass;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class UserConstants {

    public static String DISGUISE_NAME_MATCHER = "[a-zA-Z0-9_]{2,16}";

    public static String getDisplayNameWithColorAndVanish(Profile profile) {
        return (profile.getStaffSettings().isVanished() ? CC.GRAY + "*" : "") + getDisplayNameWithColor(profile);
    }

    public static String getDisplayNameWithColor(Profile profile) {
        if (profile == UserProfile.getConsoleProfile()) return CC.RED + "Console";

        IRank rank = profile.getDisplayRank();
        return getRankColor(rank) + (rank.isBold() ? CC.BOLD : "") + (rank.isItalic() ? CC.ITALIC : "") + profile.getDisplayName();
    }

    public static ChatColor getRankColor(IRank rank) {
        return ChatColor.valueOf(rank.getColor().toUpperCase());
    }

    public static String getNameWithColor(Profile profile) {
        if (profile == UserProfile.getConsoleProfile()) return CC.RED + "Console";

        IRank rank = profile.getHighestVisibleRank();
        return getRankColor(rank) + (rank.isBold() ? CC.BOLD : "") + (rank.isItalic() ? CC.ITALIC : "") + profile.getName();
    }

    public static void reloadPlayer(Player player) {
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
        if (Locale.SERVER_PLAYER_DISPLAYNAME.getBoolean()) player.setDisplayName(profile.getDisplayRank().getPrefix() + getDisplayNameWithColor(profile));
        if (Locale.SERVER_PLAYER_LISTNAME.getBoolean()) player.setPlayerListName(getDisplayNameWithColor(profile));
    }

    public static void reloadPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) reloadPlayer(player);
    }

    public static boolean isCommandDisabled(String command) {
        if (Locale.DISABLED_COMMANDS_MATCH.getStringList().contains(command)) return true;
        return Locale.DISABLED_COMMANDS_CONTAINS.getStringList().stream().anyMatch(command::contains);
    }

}
