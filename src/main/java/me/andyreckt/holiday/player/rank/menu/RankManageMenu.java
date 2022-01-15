package me.andyreckt.holiday.player.rank.menu;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.Menu;
import io.github.zowpy.menu.buttons.BooleanButton;
import io.github.zowpy.menu.buttons.DisplayButton;
import io.github.zowpy.menu.buttons.Glass;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RankManageMenu extends Menu {

    private final Rank rank;

    public RankManageMenu(Rank rank) {
        this.rank = rank;
        this.setPlaceholder(true);
        this.setAutoUpdate(true);
        this.setUpdateAfterClick(true);
    }

    @Override
    public String getTitle(Player paramPlayer) {
        return CC.LIGHT_PURPLE + "Rank Manager";
    }

    @Override
    public Map<Integer, Button> getButtons(Player paramPlayer) {
        Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player p0) {
                return new ItemBuilder(Material.WOOL)
                        .displayname(rank.getColor() + "Current Color")
                        .damage(StringUtil.convertChatColorToWoolData(rank.getColor()))
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                new RankColorMenu(rank).openMenu(player);
            }
        });

        Consumer<Rank> save = rank -> {
            this.rank.save();
            new RankManageMenu(this.rank).openMenu(paramPlayer);
        };

        toReturn.put(10, new BooleanButton<>(rank, "Bold", Rank::setBold, Rank::isBold, save));
        toReturn.put(11, new BooleanButton<>(rank, "Italic", Rank::setItalic, Rank::isItalic, save));
        toReturn.put(12, new BooleanButton<>(rank, "Visible", Rank::setVisible, Rank::isVisible, save));

        toReturn.put(14, new BooleanButton<>(rank, "Staff", Rank::setStaff, Rank::isStaff, save));
        toReturn.put(15, new BooleanButton<>(rank, "Admin", Rank::setAdmin, Rank::isAdmin, save));
        toReturn.put(16, new BooleanButton<>(rank, "Dev", Rank::setDev, Rank::isDev, save));

        toReturn.put(21, new DisplayButton(new ItemBuilder(Material.PAPER, "&dPrefix").lore(rank.getPrefix()).build()));
        toReturn.put(22, new DisplayButton(new ItemBuilder(Material.PAPER, "&dDisplayName").lore(rank.getDisplayName() == null ? "&fNone" : rank.getDisplayName()).build()));
        toReturn.put(23, new DisplayButton(new ItemBuilder(Material.PAPER, "&dSuffix").lore(rank.getSuffix()).build()));

        return toReturn;
    }
}
