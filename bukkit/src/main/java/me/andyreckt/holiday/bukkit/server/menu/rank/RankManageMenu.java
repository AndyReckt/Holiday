package me.andyreckt.holiday.bukkit.server.menu.rank;

import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.GlassMenu;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.buttons.BooleanButton;
import me.andyreckt.holiday.bukkit.util.menu.buttons.ConversationButton;
import me.andyreckt.holiday.bukkit.util.menu.buttons.EasyButton;
import me.andyreckt.holiday.bukkit.util.menu.buttons.NumberButton;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.text.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RankManageMenu extends GlassMenu {

    private final IRank rank;

    public RankManageMenu(IRank rank) {
        this.rank = rank;
        this.setUpdateAfterClick(true);
    }

    @Override
    public int getGlassColor() {
        return 7;
    }

    @Override
    public Map<Integer, Button> getAllButtons(Player player) {
        API api = Holiday.getInstance().getApi();
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(10, new ConversationButton<>(
                new ItemBuilder(Material.SIGN)
                        .displayname(CC.SECONDARY + "Rename Rank")
                        .lore(CC.GRAY + "Click to rename this rank.")
                        .build(),
                rank, Locale.RANK_ENTER_NAME.getString(),
                (x, pair) -> {
                    rank.setName(pair.getB());
                    api.saveRank(rank);
                    pair.getA().getForWhom().sendRawMessage(Locale.RANK_ENTER_NAME_SUCCESS.getString().replace("%name%", pair.getB()));
                    new RankManageMenu(rank).openMenu(player);
                })
        );

        buttons.put(11, new ConversationButton<>(
                new ItemBuilder(Material.NAME_TAG)
                        .displayname(CC.SECONDARY + "Set Display Name")
                        .lore(CC.GRAY + "Click to change the display name of this rank.")
                        .build(),
                rank, Locale.RANK_ENTER_DISPLAY_NAME.getString(),
                (x, pair) -> {
                    rank.setDisplayName(pair.getB());
                    api.saveRank(rank);
                    pair.getA().getForWhom().sendRawMessage(Locale.RANK_ENTER_DISPLAY_NAME_SUCCESS.getString().replace("%name%", CC.translate(pair.getB())));
                    new RankManageMenu(rank).openMenu(player);
                })
        );

        buttons.put(12, new EasyButton(
                new ItemBuilder(Material.WOOL)
                        .displayname(CC.SECONDARY + "Set Color")
                        .durability(StringUtil.convertChatColorToWoolData(UserConstants.getRankColor(rank)))
                        .lore(CC.GRAY + "Click to change the color of this rank.")
                        .build(),
                (u) -> new RankColorMenu(rank).openMenu(player))
        );

        buttons.put(13, new ConversationButton<>(
                new ItemBuilder(Material.PAINTING)
                        .displayname(CC.SECONDARY + "Set Prefix")
                        .lore(CC.GRAY + "Click to change the prefix of this rank.")
                        .build(),
                rank, Locale.RANK_ENTER_PREFIX.getString(),
                (x, pair) -> {
                    rank.setPrefix(pair.getB());
                    api.saveRank(rank);
                    pair.getA().getForWhom().sendRawMessage(Locale.RANK_ENTER_PREFIX_SUCCESS.getString().replace("%prefix%", CC.translate(pair.getB())));
                    new RankManageMenu(rank).openMenu(player);
                })
        );

        buttons.put(14, new ConversationButton<>(
                new ItemBuilder(Material.PAINTING)
                        .displayname(CC.SECONDARY + "Set Suffix")
                        .lore(CC.GRAY + "Click to change the suffix of this rank.")
                        .build(),
                rank, Locale.RANK_ENTER_SUFFIX.getString(),
                (x, pair) -> {
                    rank.setSuffix(pair.getB());
                    api.saveRank(rank);
                    pair.getA().getForWhom().sendRawMessage(Locale.RANK_ENTER_SUFFIX_SUCCESS.getString().replace("%suffix%", CC.translate(pair.getB())));
                    new RankManageMenu(rank).openMenu(player);
                })
        );

        buttons.put(15, new NumberButton<>(rank, Material.GOLD_NUGGET,
                CC.SECONDARY + "Weight", "Click to change the weight of this rank.",
                (x, i) -> {
                    rank.setPriority(i);
                    api.saveRank(rank);
                    new RankManageMenu(rank).openMenu(player);
                }, (x) -> rank.getPriority())
        );

        buttons.put(16, new BooleanButton<>(
                new ItemBuilder(Material.SLIME_BALL)
                        .displayname(CC.SECONDARY + "Visible")
                        .lore(CC.GRAY + "Click to change the visibility of this rank.")
                        .build(),
                rank, "visibility",
                (x, bool) -> {
                    rank.setVisible(bool);
                    api.saveRank(rank);
                    new RankManageMenu(rank).openMenu(player);
                }, (x) -> rank.isVisible())
        );

        buttons.put(19, new BooleanButton<>(
                new ItemBuilder(Material.EMPTY_MAP)
                        .displayname(CC.SECONDARY + "Bold")
                        .lore(CC.GRAY + "Click to change the bold status of this rank.")
                        .build(),
                rank, "bold",
                (x, bool) -> {
                    rank.setBold(bool);
                    api.saveRank(rank);
                    new RankManageMenu(rank).openMenu(player);
                }, (x) -> rank.isBold())
        );

        buttons.put(20, new EasyButton(
                new ItemBuilder(Material.BOOK)
                        .displayname(CC.SECONDARY + "Permissions")
                        .lore(CC.GRAY + "Click to change the permissions of this rank.")
                        .build(),
                (u) -> new RankPermissionMenu(rank).openMenu(player))
        );

        buttons.put(21, new BooleanButton<>(
                new ItemBuilder(Material.RAW_FISH)
                        .durability(3)
                        .displayname(CC.SECONDARY + "Staff")
                        .lore(CC.GRAY + "Click to change the staff status of this rank.")
                        .build(),
                rank, "staff status",
                (x, bool) -> {
                    rank.setStaff(bool);
                    api.saveRank(rank);
                    new RankManageMenu(rank).openMenu(player);
                }, (x) -> rank.isStaff())
        );

        buttons.put(22, new BooleanButton<>(
                new ItemBuilder(Material.REDSTONE)
                        .displayname(CC.SECONDARY + "Admin")
                        .lore(CC.GRAY + "Click to change the admin status of this rank.")
                        .build(),
                rank, "admin status",
                (x, bool) -> {
                    rank.setAdmin(bool);
                    api.saveRank(rank);
                    new RankManageMenu(rank).openMenu(player);
                }, (x) -> rank.isAdmin())
        );

        buttons.put(23, new BooleanButton<>(
                new ItemBuilder(Material.BEDROCK)
                        .displayname(CC.SECONDARY + "OP")
                        .lore(CC.GRAY + "Click to change the operator status of this rank.")
                        .build(),
                rank, "operator status",
                (x, bool) -> {
                    rank.setOp(bool);
                    api.saveRank(rank);
                    new RankManageMenu(rank).openMenu(player);
                }, (x) -> rank.isOp())
        );

        buttons.put(24, new EasyButton(
                new ItemBuilder(Material.BOOK_AND_QUILL)
                        .displayname(CC.SECONDARY + "Inheritances")
                        .lore(CC.GRAY + "Click to change the inheritances of this rank.")
                        .build(),
                (u) -> new RankInheritanceMenu(rank).openMenu(player))
        );

        buttons.put(25, new BooleanButton<>(
                new ItemBuilder(Material.PAPER)
                        .displayname(CC.SECONDARY + "Italic")
                        .lore(CC.GRAY + "Click to change the italic status of this rank.")
                        .build(),
                rank, "bold",
                (x, bool) -> {
                    rank.setItalic(bool);
                    api.saveRank(rank);
                    new RankManageMenu(rank).openMenu(player);
                }, (x) -> rank.isItalic())
        );

        return buttons;
    }

    @Override
    public Menu backButton() {
        return new RankManagerMenu();
    }

    @Override
    public String getTitle(Player paramPlayer) {
        return "Rank Editor - " + rank.getName();
    }
}
