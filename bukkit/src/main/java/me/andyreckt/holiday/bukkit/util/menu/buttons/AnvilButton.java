package me.andyreckt.holiday.bukkit.util.menu.buttons;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class AnvilButton extends Button {

    private final ItemStack item;
    private final String menuName, initialText;
    private final BiConsumer<Player, String> action;

    public AnvilButton(ItemStack item, String menuName, String initialText, BiConsumer<Player, String> action) {
        this.item = item;
        this.menuName = menuName;
        this.initialText = initialText;
        this.action = action;
    }


    @Override
    public ItemStack getButtonItem(Player p0) {
        return this.item;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        player.closeInventory();
        new AnvilGUI.Builder()
                .onComplete((p, text) -> {
                    action.accept(p, text);
                    return AnvilGUI.Response.close();
                })
                .title(menuName)
                .text(this.initialText)
                .plugin(Holiday.getInstance())
                .open(player);
    }

}
