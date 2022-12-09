package me.andyreckt.holiday.bukkit.util.menu.buttons;

import me.andyreckt.holiday.bukkit.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class DisplayButton extends Button {
    private ItemStack itemStack;
    private boolean cancel;

    public DisplayButton(final ItemStack itemStack, final boolean cancel) {
        this.itemStack = itemStack;
        this.cancel = cancel;
    }

    public DisplayButton(final ItemStack itemStack) {
        this.itemStack = itemStack;
        this.cancel = true;
    }

    @Override
    public ItemStack getButtonItem(final Player player) {
        if (this.itemStack == null) {
            return new ItemStack(Material.AIR);
        }
        return this.itemStack;
    }

    @Override
    public boolean shouldCancel(final Player player, final int slot, final ClickType clickType) {
        return this.cancel;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public void setItemStack(final ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public boolean isCancel() {
        return this.cancel;
    }

    public void setCancel(final boolean cancel) {
        this.cancel = cancel;
    }
}
