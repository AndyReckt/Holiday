package me.andyreckt.holiday.bukkit.server.placeholder;

import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.chat.ChatManager;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.duration.Duration;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PlaceholderAPIExpansion extends PlaceholderExpansion {
    private final Holiday plugin;

    @Override
    public @NotNull String getIdentifier() {
        return "holiday";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return "AndyReckt";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String getRequiredPlugin() {
        return null;
    }

    @Override
    public String onPlaceholderRequest(Player player, String param) {
        API api = plugin.getApi();
        Profile profile = api.getProfile(player.getUniqueId());
        switch (param.toLowerCase()) {
            case "server_name": return plugin.getThisServer().getServerName();
            case "chat_status": {
                ChatManager ch = plugin.getChatManager();
                String status = ch.isChatMuted() ? x("&cMuted") : ch.getChatDelay() > 0L ? CC.PRIMARY + "Delayed (" + TimeUtil.millisToSmallRoundedTime(ch.getChatDelay()) + ")" : x("&aEnabled");
                return CC.translate(status);
            }
            case "player_displayname_color": return UserConstants.getDisplayNameWithColor(profile);
            case "player_displayname": return x(profile.getDisplayName());
            case "player_highest_visible_rank_name": return x(profile.getHighestVisibleRank().getName());
            case "player_highest_visible_rank_displayname": return x(profile.getHighestVisibleRank().getDisplayName());
            case "player_highest_rank_name": return x(profile.getHighestRank().getName());
            case "player_highest_rank_displayname": return x(profile.getHighestRank().getDisplayName());
            case "player_highest_visible_grant_remaining_time": return Duration.of(profile.getHighestVisibleGrant().getRemainingTime()).getFormatted();
            case "player_highest_visible_grant_remaining_time_rounded": return  Duration.of(profile.getHighestVisibleGrant().getRemainingTime()).toRoundedTime();
            case "player_highest_visible_grant_remaining_time_shortened": return  Duration.of(profile.getHighestVisibleGrant().getRemainingTime()).toSmallRoundedTime();
            case "player_prefix": return x(profile.getHighestVisibleRank().getPrefix());
            case "player_suffix": return x(profile.getHighestVisibleRank().getSuffix());
            case "player_vanish": return profile.getStaffSettings().isVanished() ? x("&aEnabled") : x("&cDisabled");
            case "player_staffmode": return profile.getStaffSettings().isStaffMode() ? x("&aEnabled") :  x("&cDisabled");
            case "player_chat_mode": return profile.getStaffSettings().isAdminChat() ? x("&cAdmin") : profile.getStaffSettings().isStaffChat() ? (CC.SECONDARY + "Staff") : (CC.CHAT + "Normal");
        }
        return null;
    }

    private String x(String s) {
        return CC.translate(s);
    }
}
