package me.andyreckt.holiday.bukkit.server.menu.rank;

import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.PermissionUpdatePacket;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.item.Heads;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.buttons.ConversationButton;
import me.andyreckt.holiday.bukkit.util.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.text.StringUtil;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RankInheritanceMenu extends PaginatedMenu {

    private final IRank rank;

    public RankInheritanceMenu(IRank rank) {
        this.rank = rank;
    }

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return "&7&o... &7" + rank.getName() + " - Inheritance";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        Map<Integer, Button> buttons = new HashMap<>();
        rank.getChilds().forEach(inheritedRank -> buttons.put(buttons.size(), new ChildButton(rank, inheritedRank)));
        buttons.put(buttons.size(), new ConversationButton<>(
                new ItemBuilder(Material.SKULL_ITEM)
                        .durability(SkullType.PLAYER.ordinal())
                        .texture(Heads.LIME_PLUS.getBase())
                        .displayname(CC.SECONDARY + "Add Inheritance")
                        .lore(CC.GRAY + "Click to add a new inheritance.")
                        .build(),
                null, Locale.RANK_ENTER_PERMISSION.getString(),
                (x, pair) -> {
                    IRank child = Holiday.getInstance().getApi().getRank(pair.getB());
                    if (child == null) {
                        pair.getA().getForWhom().sendRawMessage(Locale.RANK_NOT_FOUND.getString());
                        return;
                    }
                    rank.addChild(child.getUuid());
                    Holiday.getInstance().getApi().saveRank(rank);
                    PacketHandler.send(new PermissionUpdatePacket());
                    pair.getA().getForWhom().sendRawMessage(Locale.RANK_INHERITANCE_ADDED.getString()
                            .replace("%rank%", CC.translate(rank.getDisplayName()))
                            .replace("%child%", CC.translate(child.getDisplayName())));
                    new RankInheritanceMenu(rank).openMenu(p0);
                })
        );
        return buttons;
    }

    @Override
    public Menu backButton() {
        return new RankManageMenu(rank);
    }

    private static class ChildButton extends Button {

        private final IRank rank;
        private final IRank child;

        public ChildButton(IRank rank, UUID child) {
            this.rank = rank;
            this.child = Holiday.getInstance().getApi().getRank(child);
        }


        @Override
        public ItemStack getButtonItem(Player p0) {
            ChatColor color = UserConstants.getRankColor(child);
            return new ItemBuilder(Material.WOOL)
                    .durability(StringUtil.convertChatColorToWoolData(color))
                    .displayname(child.getDisplayName())
                    .lore("", "&7&oClick to remove this child.")
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            rank.removeChild(child.getUuid());
            Holiday.getInstance().getApi().saveRank(rank);
            PacketHandler.send(new PermissionUpdatePacket());
            new RankInheritanceMenu(rank).openMenu(player);
        }
    }

}
