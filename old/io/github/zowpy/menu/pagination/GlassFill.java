package io.github.zowpy.menu.pagination;

import me.andyreckt.holiday.utils.ItemBuilder;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.*;
import io.github.zowpy.menu.*;
import org.bukkit.event.inventory.*;

public class GlassFill extends Button
{
    private PaginatedMenu menu;
    
    @Override
    public ItemStack getButtonItem(final Player player) {
        final ItemBuilder item = new ItemBuilder(Material.STAINED_GLASS_PANE);
        item.durability(7);
        item.displayname(" ");
        return item.build();
    }
    
    @Override
    public void clicked(final Player player, final int i, final ClickType clickType, final int hb) {
    }
    
    public GlassFill(final PaginatedMenu menu) {
        this.menu = menu;
    }
}
