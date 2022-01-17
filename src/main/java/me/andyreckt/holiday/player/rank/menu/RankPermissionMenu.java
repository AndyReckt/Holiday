package me.andyreckt.holiday.player.rank.menu;

import com.google.common.collect.ImmutableMap;
import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.Menu;
import io.github.zowpy.menu.buttons.DisplayButton;
import io.github.zowpy.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RankPermissionMenu extends PaginatedMenu {

    final Rank rank;

    public RankPermissionMenu(Rank rank) {
        this.rank = rank;
    }

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return "&dRank permissions";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        if (rank.getPermissions() == null || rank.getPermissions().isEmpty()) return new HashMap<>();
        Map<Integer, Button> toReturn = new HashMap<>();
        int i = 0;
        for (String perm : rank.getPermissions()) {
            toReturn.put(i++, new DisplayButton(new ItemBuilder(Material.WRITTEN_BOOK).glow().displayname("&e" + perm).build()));
        }
        return toReturn;
    }
}
