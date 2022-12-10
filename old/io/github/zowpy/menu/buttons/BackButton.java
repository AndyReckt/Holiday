package io.github.zowpy.menu.buttons;

import me.andyreckt.holiday.utils.ItemBuilder;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import io.github.zowpy.menu.*;
import org.bukkit.*;
import org.bukkit.event.inventory.*;

public class BackButton extends Button
{
    private Menu back;
    
    @Override
    public ItemStack getButtonItem(final Player player) {
        final ItemBuilder item = new ItemBuilder(Material.ARROW).displayname(ChatColor.translateAlternateColorCodes('&', "&cGo back"));
        return item.build();
    }
    
    @Override
    public void clicked(final Player player, final int i, final ClickType clickType, final int hb) {
        Button.playNeutral(player);
        this.back.openMenu(player);
    }
    
    public BackButton(final Menu back) {
        this.back = back;
    }
}
