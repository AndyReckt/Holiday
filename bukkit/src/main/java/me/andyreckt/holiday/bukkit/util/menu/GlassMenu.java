package me.andyreckt.holiday.bukkit.util.menu;

import me.andyreckt.holiday.bukkit.util.menu.buttons.Glass;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class GlassMenu extends Menu {

    @Override
    public Map<Integer, Button> getButtons(Player paramPlayer) {

        final HashMap<Integer, Button> buttons = new HashMap<>(getAllButtons(paramPlayer));

        int size = size(buttons);

        for (int i : new int[]{0, 1, 7, 8, 9, 17, size - 18, size - 10, size - 9, size - 8, size - 2, size - 1}) {
            buttons.put(i, new Glass(getGlassColor()));
        }

        return buttons;

    }

    public abstract int getGlassColor();

    public abstract Map<Integer, Button> getAllButtons(Player player);

}
