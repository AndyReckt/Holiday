package me.andyreckt.holiday.bukkit.util.menu.buttons;

import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CloseButton extends Button {
    @Override
    public ItemStack getButtonItem(final Player player) {
        final ItemBuilder item = new ItemBuilder(Material.INK_SACK).damage(1).displayname(ChatColor.translateAlternateColorCodes('&', "&cClose"));
        return item.build();
    }

    @Override
    public void clicked(final Player player, final int i, final ClickType clickType, final int hb) {
        Button.playNeutral(player);
        player.closeInventory();
    }
}
