package me.andyreckt.holiday.bukkit.util.menu.buttons;

import me.andyreckt.holiday.bukkit.util.menu.Button;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class EasyButton extends Button {

    final ItemStack itemStack;
    final Consumer<Player> consumer;
    final boolean update;

    public EasyButton(ItemStack itemStack, Consumer<Player> consumer) {
        this.itemStack = itemStack;
        this.consumer = consumer;
        this.update = false;
    }

    @Override
    public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
        return update;
    }

    @Override
    public ItemStack getButtonItem(Player p0) {
        return itemStack;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        this.consumer.accept(player);
    }
}
