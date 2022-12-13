package me.andyreckt.holiday.bukkit.server.menu.rank;

import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.GlassMenu;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.text.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RankManagerMenu extends GlassMenu {
    @Override
    public int getGlassColor() {
        return 7;
    }

    @Override
    public Map<Integer, Button> getAllButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 10;
        for (IRank rank : Holiday.getInstance().getApi().getRanksSorted()) {
            if ((i + 1) % 9 == 0) {
                i += 2;
            }
            buttons.put(i, new RankButton(rank));
            i++;
        }

        return buttons;
    }

    @Override
    public String getTitle(Player paramPlayer) {
        return "Rank Editor";
    }

    private static class RankButton extends Button {
        private final IRank rank;
        public RankButton(IRank rank) {
            this.rank = rank;
        }

        @Override
        public ItemStack getButtonItem(Player p0) {
            ChatColor color = Holiday.getInstance().getRankColor(rank);
            return new ItemBuilder(Material.WOOL)
                    .durability(StringUtil.convertChatColorToWoolData(color))
                    .displayname(rank.getDisplayName())
                    .lore(CC.MENU_BAR,
                            CC.PRIMARY + "Name: " + CC.SECONDARY + rank.getName(),
                            CC.PRIMARY + "DisplayName: " + CC.SECONDARY + rank.getDisplayName(),
                            "",
                            CC.PRIMARY + "Weight: " + CC.SECONDARY + rank.getPriority(),
                            CC.PRIMARY + "Prefix: " + CC.SECONDARY + rank.getPrefix(),
                            CC.PRIMARY + "Suffix: " + CC.SECONDARY + rank.getSuffix(),
                            CC.PRIMARY + "Color: " + color + color.name(),
                            CC.PRIMARY + "Bold: " + yesNo(rank.isBold()),
                            CC.PRIMARY + "Italic: " + yesNo(rank.isItalic()),
                            "",
                            CC.PRIMARY + "Default: " + yesNo(rank.isDefault()),
                            CC.PRIMARY + "Staff: " + yesNo(rank.isStaff()),
                            CC.PRIMARY + "Admin: " + yesNo(rank.isAdmin()),
                            CC.PRIMARY + "Op: " + yesNo(rank.isOp()),
                            CC.PRIMARY + "Visible: " + yesNo(rank.isVisible()),
                            CC.MENU_BAR)
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new RankManageMenu(rank).openMenu(player);
        }

        private String yesNo(boolean bool) {
            return bool ? "&aYes" : "&cNo";
        }

    }
}
