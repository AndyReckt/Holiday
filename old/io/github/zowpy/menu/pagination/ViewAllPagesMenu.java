package io.github.zowpy.menu.pagination;

import lombok.*;
import org.bukkit.entity.*;
import io.github.zowpy.menu.*;
import java.util.*;
import io.github.zowpy.menu.buttons.*;

public class ViewAllPagesMenu extends Menu
{
    @NonNull
    PaginatedMenu menu;
    
    @Override
    public String getTitle(final Player player) {
        return "&cJump to page";
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
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
    
    public ViewAllPagesMenu(@NonNull final PaginatedMenu menu) {
        this.menu = menu;
    }
    
    @NonNull
    public PaginatedMenu getMenu() {
        return this.menu;
    }
}
