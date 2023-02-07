package me.andyreckt.holiday.bukkit.server.menu.rank;

import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.item.Heads;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.buttons.ConversationButton;
import me.andyreckt.holiday.bukkit.util.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.text.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RankManagerMenu extends PaginatedMenu {
    @Override
    public int getGlassColor() {
        return 8;
    }

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return "Rank Editor";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        Map<Integer, Button> buttons = new HashMap<>();
        for (IRank rank : Holiday.getInstance().getApi().getRanksSorted()) {
            buttons.put(buttons.size(), new RankButton(rank));
        }
        buttons.put(buttons.size(), new ConversationButton<>(
            new ItemBuilder(Material.SKULL_ITEM)
                .durability(SkullType.PLAYER.ordinal())
                .texture(Heads.LIME_PLUS.getBase())
                .displayname(CC.SECONDARY + "Create Rank")
                .lore(CC.GRAY + "Click to create a new rank.")
                .build(),
            null, Locale.RANK_ENTER_NAME.getString(),
            (x, pair) -> {
                IRank rank = Holiday.getInstance().getApi().createRank(pair.getB());
                Holiday.getInstance().getApi().saveRank(rank);
                pair.getA().getForWhom().sendRawMessage(Locale.RANK_SUCCESSFULLY_CREATED.getString().replace("%rank%", pair.getB()));
                new RankManageMenu(rank).openMenu(p0);
            })
        );

        return buttons;
    }

    private static class RankButton extends Button {
        private final IRank rank;
        public RankButton(IRank rank) {
            this.rank = rank;
        }

        @Override
        public ItemStack getButtonItem(Player p0) {
            ChatColor color = UserConstants.getRankColor(rank);
            return new ItemBuilder(Material.WOOL)
                    .durability(StringUtil.convertChatColorToWoolData(color))
                    .displayname(rank.getDisplayName())
                    .lore(" ",
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Name: " + CC.SECONDARY + rank.getName(),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "DisplayName: " + CC.SECONDARY + rank.getDisplayName(),
                            "",
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Weight: " + CC.SECONDARY + rank.getPriority(),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Prefix: " + CC.SECONDARY + rank.getPrefix(),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Suffix: " + CC.SECONDARY + rank.getSuffix(),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Color: " + color + color.name(),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Bold: " + yesNo(rank.isBold()),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Italic: " + yesNo(rank.isItalic()),
                            "",
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Default: " + yesNo(rank.isDefault()),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Staff: " + yesNo(rank.isStaff()),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Admin: " + yesNo(rank.isAdmin()),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Op: " + yesNo(rank.isOp()),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Visible: " + yesNo(rank.isVisible()))
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
