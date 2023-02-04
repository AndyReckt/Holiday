package me.andyreckt.holiday.bukkit.server.menu.rank;

import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.PermissionUpdatePacket;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.item.Heads;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.buttons.ConversationButton;
import me.andyreckt.holiday.bukkit.util.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RankPermissionMenu extends PaginatedMenu {

    private final IRank rank;

    public RankPermissionMenu(IRank rank) {
        this.rank = rank;
    }

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return "&7&o... &7" + rank.getName() + " - Permissions";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        Map<Integer, Button> buttons = new HashMap<>();
        rank.getPermissions().forEach(perm -> buttons.put(buttons.size(), new PermissionButton(rank, perm)));
        buttons.put(buttons.size(), new ConversationButton<>(
                new ItemBuilder(Material.SKULL_ITEM)
                        .durability(SkullType.PLAYER.ordinal())
                        .texture(Heads.LIME_PLUS.getBase())
                        .displayname(CC.SECONDARY + "Add Permission")
                        .lore(CC.GRAY + "Click to add a new permission.")
                        .build(),
                null, Locale.RANK_ENTER_PERMISSION.getString(),
                (x, pair) -> {
                    rank.addPermission(pair.getB());
                    Holiday.getInstance().getApi().saveRank(rank);
                    PacketHandler.send(new PermissionUpdatePacket());
                    pair.getA().getForWhom().sendRawMessage(Locale.RANK_PERMISSION_ADDED.getString()
                            .replace("%rank%", CC.translate(rank.getDisplayName()))
                            .replace("%permission%", pair.getB()));
                    new RankPermissionMenu(rank).openMenu(p0);
                })
        );
        return buttons;
    }

    @Override
    public Menu backButton() {
        return new RankManageMenu(rank);
    }

    private static class PermissionButton extends Button {

        private final IRank rank;
        private final String permission;

        public PermissionButton(IRank rank, String permission) {
            this.rank = rank;
            this.permission = permission;
        }


        @Override
        public ItemStack getButtonItem(Player p0) {
            return new ItemBuilder(Material.PAPER)
                .displayname("&7" + permission)
                .lore("", "&7&oClick to remove this permission.")
                .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            rank.removePermission(permission);
            Holiday.getInstance().getApi().saveRank(rank);
            PacketHandler.send(new PermissionUpdatePacket());
            new RankPermissionMenu(rank).openMenu(player);
        }
    }

}
