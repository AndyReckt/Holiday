package me.andyreckt.holiday.bukkit.util.menu;

import me.andyreckt.holiday.bukkit.util.menu.buttons.BackButton;
import me.andyreckt.holiday.bukkit.util.menu.buttons.Glass;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class GlassMenu extends Menu {

    @Override
    public Map<Integer, Button> getButtons(Player paramPlayer) {

        final HashMap<Integer, Button> buttons = new HashMap<>(getAllButtons(paramPlayer));

        int size = s(buttons) + 9;

        for (int i : new int[]{0, 1, 7, 8, 9, 17, size - 18, size - 10, size - 9, size - 8, size - 2, size - 1}) {
            buttons.put(i, new Glass(getGlassColor()));
        }

        if (this.backButton() != null) {
            buttons.put(size - 5, new BackButton(this.backButton()));
        }

        return buttons;

    }

    public abstract int getGlassColor();

    public abstract Map<Integer, Button> getAllButtons(Player player);


    private int s(Map<Integer, Button> buttons) {
        int x = 0;
        for (int i : buttons.keySet()) {
            if (i > x)
                x = i;
        }
        return (x / 9 + 1) * 9;
    }

    public Menu backButton() {
        return null;
    }

}
