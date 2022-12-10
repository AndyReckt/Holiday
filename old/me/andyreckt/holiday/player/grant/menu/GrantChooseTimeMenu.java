package me.andyreckt.holiday.player.grant.menu;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.Menu;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.grant.menu.button.TimeButton;
import me.andyreckt.holiday.player.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GrantChooseTimeMenu extends Menu {

    final Profile profile;
    final Rank rank;

    public GrantChooseTimeMenu(Profile profile, Rank rank) {
        this.profile = profile;
        this.rank = rank;
    }

    @Override
    public String getTitle(Player paramPlayer) {
        return "&bChoose a duration";
    }

    @Override
    public Map<Integer, Button> getButtons(Player paramPlayer) {
        Map<Integer, Button> toReturn = new HashMap<>();
        toReturn.put(0, new TimeButton(ChatColor.GOLD, "5m", profile, rank));
        toReturn.put(1, new TimeButton(ChatColor.YELLOW, "30m", profile, rank));
        toReturn.put(2, new TimeButton(ChatColor.AQUA, "1h", profile, rank));
        toReturn.put(3, new TimeButton(ChatColor.LIGHT_PURPLE, "12h", profile, rank));
        toReturn.put(4, new TimeButton(ChatColor.DARK_PURPLE, "1d", profile, rank));
        toReturn.put(5, new TimeButton(ChatColor.RED, "3d", profile, rank));
        toReturn.put(6, new TimeButton(ChatColor.DARK_GRAY, "7d", profile, rank));
        toReturn.put(7, new TimeButton(ChatColor.DARK_AQUA, "30d", profile, rank));
        toReturn.put(8, new TimeButton(ChatColor.BLUE, "Permanent", profile, rank));
        return toReturn;
    }
}
