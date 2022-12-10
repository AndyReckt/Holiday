package me.andyreckt.holiday.player.rank.menu;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.buttons.DisplayButton;
import io.github.zowpy.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RankChildMenu extends PaginatedMenu {

    final Rank rank;

    public RankChildMenu(Rank rank) {
        this.rank = rank;
    }

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return "&dRank permissions";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        if (rank.getChilds() == null || rank.getChilds().isEmpty()) return new HashMap<>();
        Map<Integer, Button> toReturn = new HashMap<>();
        int i = 0;
        for (UUID uuid : rank.getChilds()) {
            Rank rank = Holiday.getInstance().getRankHandler().getFromId(uuid);
            toReturn.put(i++, new DisplayButton(new ItemBuilder(Material.WOOL)
                    .damage(StringUtil.convertChatColorToWoolData(rank.getColor()))
                    .displayname(rank.getDisplayName())
                    .build()));
        }
        return toReturn;
    }
}
