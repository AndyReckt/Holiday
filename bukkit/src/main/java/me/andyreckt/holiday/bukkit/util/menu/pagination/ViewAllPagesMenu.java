package me.andyreckt.holiday.bukkit.util.menu.pagination;

import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.buttons.BackButton;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ViewAllPagesMenu extends Menu {
    @NonNull
    PaginatedMenu menu;

    public ViewAllPagesMenu(@NonNull final PaginatedMenu menu) {
        if (menu == null) {
            throw new NullPointerException("menu is marked non-null but is null");
        }
        this.menu = menu;
    }

    @Override
    public String getTitle(final Player player) {
        return "&cJump to page";
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new BackButton(this.menu));
        int index = 10;
        for (int i = 1; i <= this.menu.getPages(player); ++i) {
            buttons.put(index++, new JumpToPageButton(i, this.menu, this.menu.getPage() == i));
            if ((index - 8) % 9 == 0) {
                index += 2;
            }
        }
        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return false;
    }

    @NonNull
    public PaginatedMenu getMenu() {
        return this.menu;
    }
}
