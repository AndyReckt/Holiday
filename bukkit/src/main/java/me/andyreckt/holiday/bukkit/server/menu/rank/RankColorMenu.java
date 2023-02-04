package me.andyreckt.holiday.bukkit.server.menu.rank;

import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.GlassMenu;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.buttons.EasyButton;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.text.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class RankColorMenu extends GlassMenu {

    private final IRank rank;

    @Override
    public int getGlassColor() {
        return 15;
    }

    @Override
    public Map<Integer, Button> getAllButtons(Player player) {
        Map<Integer, Button> toReturn = new HashMap<>();
        int j = 10;
        for (int i = 0; i < 16; i++) {
            if (i == 12) continue;
            final ChatColor color = StringUtil.convertWoolDataToChatColor(i);
            if (color == null) continue;
            toReturn.put(j, new EasyButton(new ItemBuilder(Material.WOOL)
                    .damage(i == 15 ? 14 : i)
                    .displayname(color + color.name())
                    .lore(CC.GRAY + "Click to change the color to " + color + color.name() + CC.GRAY + ".")
                    .build(),
                    u -> {
                        rank.setColor(color.name());
                        Holiday.getInstance().getApi().saveRank(rank);
                        new RankManageMenu(rank).openMenu(player);
                        player.sendMessage(Locale.RANK_COLOR_UPDATED.getString().replace("%color%", color + color.name()));
                    }));
            j++;
            if (j == 17) j = 19;
            if (j == 26) j = 28;
        }

        return toReturn;
    }

    @Override
    public String getTitle(Player paramPlayer) {
        return "Rank Editor - " + rank.getName() + " - Color";
    }

    @Override
    public Menu backButton() {
        return new RankManageMenu(rank);
    }
}

