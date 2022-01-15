package me.andyreckt.holiday.player.rank.menu;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.Menu;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RankColorMenu extends Menu {
    
    final Rank rank;

    public RankColorMenu(Rank rank) {
        this.rank = rank;
    }

    @Override
    public String getTitle(Player paramPlayer) {
        return CC.AQUA + "Choose a color for the rank " + rank.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player paramPlayer) {
        Map<Integer, Button> toReturn = new HashMap<>();

        for (int i = 0; i < 16; i++) {
            if (i == 12) continue;
            int finalI = i;
            toReturn.put(i, new Button() {
                final ChatColor color = StringUtil.convertWoolDataToChatColor(finalI);

                @Override
                public ItemStack getButtonItem(Player p0) {
                    return new ItemBuilder(Material.WOOL).damage(finalI == 15 ? 14 : 15).displayname(color + color.name()).build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    rank.setColor(color);
                    rank.save();
                }
            });
        }
        
        return toReturn;
    }
}
