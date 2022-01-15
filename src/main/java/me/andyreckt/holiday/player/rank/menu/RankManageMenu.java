package me.andyreckt.holiday.player.rank.menu;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.Menu;
import io.github.zowpy.menu.buttons.BooleanButton;
import io.github.zowpy.menu.buttons.ConversationButton;
import io.github.zowpy.menu.buttons.DisplayButton;
import io.github.zowpy.menu.buttons.Glass;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
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

        toReturn.put(21, new ConversationButton<>(
                new ItemBuilder(Material.PAPER, "&dPrefix").lore(rank.getPrefix()).build(),
                rank, "&aEnter the new prefix for this rank:",
                (rank1, pair) -> {
                    rank.setPrefix(pair.getB());
                    rank.save();
                    pair.getA().getForWhom().sendRawMessage(CC.translate("&aSuccessfully changed the prefix to " + pair.getB() ));
                }
        ));
        toReturn.put(22, new ConversationButton<>(
                new ItemBuilder(Material.PAPER, "&dDisplay Name").lore(rank.getDisplayName()).build(),
                rank, "&aEnter the new display name for this rank:",
                (rank1, pair) -> {
                    rank.setDisplayName(pair.getB());
                    rank.save();
                    pair.getA().getForWhom().sendRawMessage(CC.translate("&aSuccessfully changed the displayname to " + pair.getB() ));
                }
        ));
        toReturn.put(23, new ConversationButton<>(
                new ItemBuilder(Material.PAPER, "&dSuffix").lore(rank.getSuffix()).build(),
                rank, "&aEnter the new suffix for this rank:",
                (rank1, pair) -> {
                    rank.setDisplayName(pair.getB());
                    rank.save();
                    pair.getA().getForWhom().sendRawMessage(CC.translate("&aSuccessfully changed the suffix to " + pair.getB() ));
                }
        ));

        return toReturn;
    }


}
