package io.github.zowpy.menu.buttons;

import me.andyreckt.holiday.utils.ItemBuilder;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import io.github.zowpy.menu.*;
import org.bukkit.event.inventory.*;
import org.bukkit.*;

public class ConfirmationButton extends Button
{
    private boolean confirm;
    private TypeCallback<Boolean> callback;
    private boolean closeAfterResponse;
    
    @Override
    public ItemStack getButtonItem(final Player player) {
        final ItemBuilder item = new ItemBuilder(Material.WOOL);
        item.durability(this.confirm ? 5 : 14);
        item.displayname(ChatColor.translateAlternateColorCodes('&', this.confirm ? "&aConfirm" : "&cCancel"));
        return item.build();
    }
    
    @Override
    public void clicked(final Player player, final int i, final ClickType clickType, final int hb) {
        if (this.confirm) {
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20.0f, 0.1f);
        }
        else {
            player.playSound(player.getLocation(), Sound.DIG_GRAVEL, 20.0f, 0.1f);
        }
        if (this.closeAfterResponse) {
            player.closeInventory();
        }
        this.callback.callback(this.confirm);
    }
    
    public ConfirmationButton(final boolean confirm, final TypeCallback<Boolean> callback, final boolean closeAfterResponse) {
        this.confirm = confirm;
        this.callback = callback;
        this.closeAfterResponse = closeAfterResponse;
    }
}
