package me.andyreckt.holiday.other.placeholder;

import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.server.chat.ChatHandler;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.TimeUtil;
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
        switch (param.toLowerCase()) {
            case "server_name": return plugin.getSettings().getString("SERVER.NICENAME");
            case "chat_status": {
                ChatHandler ch = plugin.getChatHandler();
                String status = ch.isChatMuted() ? "&cMuted" : ch.getChatDelay() > 0L ? CC.PRIMARY + "Delayed (" + TimeUtil.millisToSmallRoundedTime(ch.getChatDelay()) + ")" : "&aNormal";
                return CC.translate(status);
            }
            case "player_displayname_color": return plugin.getProfileHandler().getByPlayer(player).getDisplayNameWithColor();
            case "player_displayname": return plugin.getProfileHandler().getByPlayer(player).getDisplayName();
            case "player_highest_visible_rank_name": return plugin.getProfileHandler().getByPlayer(player).getHighestVisibleRank().getName();
            case "player_highest_visible_rank_displayname": return plugin.getProfileHandler().getByPlayer(player).getHighestVisibleRank().getDisplayName();
            case "player_highest_rank_name": return plugin.getProfileHandler().getByPlayer(player).getHighestRank().getName();
            case "player_highest_rank_displayname": return plugin.getProfileHandler().getByPlayer(player).getHighestRank().getDisplayName();
            case "player_highest_visible_grant_remaining_time": return TimeUtil.getDuration(plugin.getProfileHandler().getByPlayer(player).getHighestVisibleGrant().getRemainingTime());
            case "player_highest_visible_grant_remaining_time_rounded": return TimeUtil.millisToRoundedTime(plugin.getProfileHandler().getByPlayer(player).getHighestVisibleGrant().getRemainingTime());
            case "player_highest_visible_grant_remaining_time_shortened": return TimeUtil.millisToSmallRoundedTime(plugin.getProfileHandler().getByPlayer(player).getHighestVisibleGrant().getRemainingTime());
            case "player_prefix": return plugin.getProfileHandler().getByPlayer(player).getHighestVisibleRank().getPrefix();
            case "player_suffix": return plugin.getProfileHandler().getByPlayer(player).getHighestVisibleRank().getSuffix();
            case "player_vanish": return plugin.getProfileHandler().getByPlayer(player).isVanished() ? "&aEnabled" : "&cDisabled";
            case "player_staffmode": return plugin.getProfileHandler().getByPlayer(player).isInStaffMode() ? "&aEnabled" : "&cDisabled";
        }
        return null;
    }
}
