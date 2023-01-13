package me.andyreckt.holiday.bukkit.server.menu.disguise;

import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.core.user.disguise.Disguise;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.buttons.EasyButton;
import me.andyreckt.holiday.bukkit.util.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.bukkit.util.text.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DisguiseRankMenu extends PaginatedMenu {

    private final Disguise disguise;

    public DisguiseRankMenu(Disguise disguise) {
        this.disguise = disguise;
        this.setGlassColor(6);
    }

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return "Disguise Ranks";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for (IRank rank : Holiday.getInstance().getApi().getRanksSorted()) {
            if (!player.hasPermission(Perms.DISGUISE_RANK.get().replace("rank", rank.getName()))) continue;
            buttons.put(buttons.size(), new EasyButton(
                    new ItemBuilder(Material.WOOL)
                            .displayname(rank.getDisplayName())
                            .durability(StringUtil.convertChatColorToWoolData(ChatColor.valueOf(rank.getColor())))
                            .build(), o -> {
                    disguise.setDisguiseRank(rank);
                    new DisguiseMenu(disguise).openMenu(player);
            }
            ));
        }

        return buttons;
    }

    @Override
    public Menu backButton() {
        return new DisguiseMenu(disguise);
    }
}
