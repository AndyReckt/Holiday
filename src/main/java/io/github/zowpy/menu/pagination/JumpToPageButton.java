package io.github.zowpy.menu.pagination;

import me.andyreckt.holiday.utils.ItemBuilder;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import io.github.zowpy.menu.*;
import org.bukkit.*;
import org.bukkit.event.inventory.*;

public class JumpToPageButton extends Button
{
    private int page;
    private PaginatedMenu menu;
    private boolean current;
    
    @Override
    public ItemStack getButtonItem(final Player player) {
        final ItemBuilder item = new ItemBuilder(this.current ? Material.EMPTY_MAP : Material.PAPER);
        item.amount(this.page);
        item.displayname(ChatColor.translateAlternateColorCodes('&', "&7Page " + this.page));
        if (this.current) {
            item.lore("&7Current Page");
        }
        return item.build();
    }
    
    @Override
    public void clicked(final Player player, final int i, final ClickType clickType, final int hb) {
        this.menu.modPage(player, this.page - this.menu.getPage());
        Button.playNeutral(player);
    }
    
    public JumpToPageButton(final int page, final PaginatedMenu menu, final boolean current) {
        this.page = page;
        this.menu = menu;
        this.current = current;
    }
}
