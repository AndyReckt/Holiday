package io.github.zowpy.menu.pagination;

import me.andyreckt.holiday.utils.ItemBuilder;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import io.github.zowpy.menu.*;
import org.bukkit.*;
import org.bukkit.event.inventory.*;

public class PageButton extends Button
{
    private int mod;
    private PaginatedMenu menu;
    
    @Override
    public ItemStack getButtonItem(final Player player) {
        final ItemBuilder item = new ItemBuilder(Material.CARPET);
        if (this.hasNext(player)) {
            item.durability(8);
            item.displayname(ChatColor.translateAlternateColorCodes('&', (this.mod > 0) ? "&aNext page" : "&cPrevious page"));
        }
        else {
            item.durability(7);
            item.displayname(ChatColor.translateAlternateColorCodes('&', (this.mod > 0) ? "&7Last page" : "&7First page"));
        }
        return item.build();
    }
    
    @Override
    public void clicked(final Player player, final int i, final ClickType clickType, final int hb) {
        if (clickType == ClickType.RIGHT) {
            new ViewAllPagesMenu(this.menu).openMenu(player);
            Button.playNeutral(player);
        }
        else if (this.hasNext(player)) {
            this.menu.modPage(player, this.mod);
            Button.playNeutral(player);
        }
        else {
            Button.playFail(player);
        }
    }
    
    private boolean hasNext(final Player player) {
        final int pg = this.menu.getPage() + this.mod;
        return pg > 0 && this.menu.getPages(player) >= pg;
    }
    
    public PageButton(final int mod, final PaginatedMenu menu) {
        this.mod = mod;
        this.menu = menu;
    }
}
