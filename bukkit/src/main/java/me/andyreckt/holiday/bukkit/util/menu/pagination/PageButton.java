package me.andyreckt.holiday.bukkit.util.menu.pagination;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.item.Heads;
import me.andyreckt.holiday.bukkit.util.item.InventoryUpdate;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PageButton extends Button {
    private final int mod;
    private final PaginatedMenu menu;

    public PageButton(final int mod, final PaginatedMenu menu) {
        this.mod = mod;
        this.menu = menu;
    }

    @Override
    public ItemStack getButtonItem(final Player player) {
        ItemBuilder item = (this.mod > 0) ? new ItemBuilder(Heads.NEXT_PAGE.toItemStack()) : new ItemBuilder(Heads.PREVIOUS_PAGE.toItemStack());
        if (this.hasNext(player)) {
            item.displayname(ChatColor.translateAlternateColorCodes('&', (this.mod > 0) ? "&8» &7Next Page" : "&8« &7Previous Page"));
        } else {
            item = new ItemBuilder(Material.AIR);
        }
        return item.build();
    }

    @Override
    public void clicked(final Player player, final int i, final ClickType clickType, final int hb) {
        if (this.hasNext(player)) {
            this.menu.modPage(player, this.mod);
            Button.playNeutral(player);
            InventoryUpdate.updateInventory(Holiday.getInstance(), player, menu.getTitle(player));
        }
    }

    private boolean hasNext(final Player player) {
        final int pg = this.menu.getPage() + this.mod;
        return pg > 0 && this.menu.getPages(player) >= pg;
    }
}
