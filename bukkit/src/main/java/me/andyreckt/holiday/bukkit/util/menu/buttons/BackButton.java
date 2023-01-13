package me.andyreckt.holiday.bukkit.util.menu.buttons;

import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class BackButton extends Button {
    private final Menu back;

    public BackButton(final Menu back) {
        this.back = back;
    }

    @Override
    public ItemStack getButtonItem(final Player player) {
        final ItemBuilder item = new ItemBuilder(Material.ARROW).displayname(ChatColor.translateAlternateColorCodes('&', "&8« &7Go back"));
        return item.build();
    }

    @Override
    public void clicked(final Player player, final int i, final ClickType clickType, final int hb) {
        Button.playNeutral(player);
        this.back.openMenu(player);
    }
}
