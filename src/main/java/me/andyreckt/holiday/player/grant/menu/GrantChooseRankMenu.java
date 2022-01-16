package me.andyreckt.holiday.player.grant.menu;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.Menu;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.grant.menu.button.RankButton;
import me.andyreckt.holiday.player.rank.Rank;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GrantChooseRankMenu extends Menu {

    final Profile profile;

    public GrantChooseRankMenu(Profile profile) {
        this.profile = profile;
    }

    @Override
    public String getTitle(Player paramPlayer) {
        return "&bChoose a rank";
    }

    @Override
    public Map<Integer, Button> getButtons(Player paramPlayer) {
        Map<Integer, Button> toReturn = new HashMap<>();
        int i = 0;
        for (Rank rank : Holiday.getInstance().getRankHandler().ranksSorted()) {
            if (rank.isDefault()) continue;
            toReturn.put(i++, new RankButton(profile, rank));
        }
        return toReturn;
    }
}
