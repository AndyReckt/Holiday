package me.andyreckt.holiday.bukkit.server.menu.grant;

import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.bukkit.util.text.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GrantChooseRankMenu extends PaginatedMenu { //TODO: Redo Menus

    private final Profile profile;

    public GrantChooseRankMenu(Profile profile) {
        this.profile = profile;
    }

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return "&bChoose a rank";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        Map<Integer, Button> toReturn = new HashMap<>();
        int i = 0;
        for (IRank rank : Holiday.getInstance().getApi().getRanksSorted()) {
            if (rank.isDefault()) continue;
            toReturn.put(i++, new RankButton(profile, rank));
        }
        return toReturn;
    }

    static class RankButton extends Button {

        private final Profile profile;
        private final IRank rank;

        public RankButton(Profile profile, IRank rank) {
            this.profile = profile;
            this.rank = rank;
        }

        @Override
        public ItemStack getButtonItem(Player p0) {
            return new ItemBuilder(Material.WOOL)
                    .damage(StringUtil.convertChatColorToWoolData(UserConstants.getRankColor(rank)))
                    .displayname(rank.getDisplayName())
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new GrantChooseTimeMenu(profile, rank).openMenu(player);
        }
    }
}
