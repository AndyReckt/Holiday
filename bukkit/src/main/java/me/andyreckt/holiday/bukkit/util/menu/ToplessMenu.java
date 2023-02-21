package me.andyreckt.holiday.bukkit.util.menu;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class ToplessMenu extends Menu {
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        for (Map.Entry<Integer, Button> entry : getAllButtons(player).entrySet()) {
            // We add the top amount as they aren't going on the top bar here.
            int index = entry.getKey() + 8;
            if (index > 8) {
                buttons.put(index, entry.getValue());
            }
        }

        final Map<Integer, Button> topLevel = getTopLevelButtons(player);

        if (topLevel != null) {
            for (Map.Entry<Integer, Button> entry : topLevel.entrySet()) {
                int index = entry.getKey();

                if (index <= 8) {
                    buttons.put(index, entry.getValue());
                }
            }
        }


        return buttons;
    }

    public Map<Integer, Button> getTopLevelButtons(Player player) { return null; }
    public abstract Map<Integer, Button> getAllButtons(Player player);
}