package me.andyreckt.holiday.bukkit.util.menu.buttons;

import me.andyreckt.holiday.bukkit.util.item.Heads;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SoonButton extends Button {
    @Override
    public ItemStack getButtonItem(Player p0) {
        return new ItemBuilder(Heads.SOON.toItemStack()).setName("&8&lProchainement").setLore(
                "&7Cette fonctionnalité n'est toujours pas",
                "&7disponible, mais elle le sera bientôt."
        ).toItemStack();
    }
}
