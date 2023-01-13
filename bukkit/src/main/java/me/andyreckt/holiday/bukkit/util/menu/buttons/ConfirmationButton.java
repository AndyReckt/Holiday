package me.andyreckt.holiday.bukkit.util.menu.buttons;

import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.TypeCallback;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ConfirmationButton extends Button {
    private final boolean confirm;
    private final TypeCallback<Boolean> callback;
    private final boolean closeAfterResponse;

    public ConfirmationButton(final boolean confirm, final TypeCallback<Boolean> callback, final boolean closeAfterResponse) {
        this.confirm = confirm;
        this.callback = callback;
        this.closeAfterResponse = closeAfterResponse;
    }

    @Override
    public ItemStack getButtonItem(final Player player) {
        final ItemBuilder item = new ItemBuilder(Material.WOOL);
        item.damage(this.confirm ? 5 : 14);
        item.displayname(ChatColor.translateAlternateColorCodes('*', this.confirm ? "&aConfirm" : "&cCancel"));
        return item.build();
    }

    @Override
    public void clicked(final Player player, final int i, final ClickType clickType, final int hb) {
        if (this.confirm) {
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20.0f, 0.1f);
        } else {
            player.playSound(player.getLocation(), Sound.DIG_GRAVEL, 20.0f, 0.1f);
        }
        if (this.closeAfterResponse) {
            player.closeInventory();
        }
        this.callback.callback(this.confirm);
    }
}
