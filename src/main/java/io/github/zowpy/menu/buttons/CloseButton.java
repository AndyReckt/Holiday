package io.github.zowpy.menu.buttons;

import me.andyreckt.holiday.utils.ItemBuilder;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import io.github.zowpy.menu.*;
import org.bukkit.*;
import org.bukkit.event.inventory.*;

public class CloseButton extends Button
{
    @Override
    public ItemStack getButtonItem(final Player player) {
        final ItemBuilder item = new ItemBuilder(Material.INK_SACK).durability(1).displayname(ChatColor.translateAlternateColorCodes('&', "&cClose"));
        return item.build();
    }
    
    @Override
    public void clicked(final Player player, final int i, final ClickType clickType, final int hb) {
        Button.playNeutral(player);
        player.closeInventory();
    }
}
