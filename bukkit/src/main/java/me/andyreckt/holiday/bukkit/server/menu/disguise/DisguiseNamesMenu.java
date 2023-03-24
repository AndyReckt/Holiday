package me.andyreckt.holiday.bukkit.server.menu.disguise;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.disguise.Disguise;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DisguiseNamesMenu extends PaginatedMenu {
    private final Disguise disguise;

    public DisguiseNamesMenu(Disguise disguise) {
        this.disguise = disguise;
    }

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return "Choose a name";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        Map<Integer, Button> buttons = new HashMap<>();
        Holiday.getInstance().getDisguiseManager().getUnusedNames()
                .forEach(name -> buttons.put(buttons.size(), new NameButton(name, disguise)));

        return buttons;
    }

    @Override
    public Menu backButton() {
        return new DisguiseMenu(disguise);
    }

    private static class NameButton extends Button {
        private final String name;
        private final Disguise disguise;

        public NameButton(String name, Disguise disguise) {
            this.name = name;
            this.disguise = disguise;
        }

        @Override
        public ItemStack getButtonItem(Player p0) {
            return new ItemBuilder(Material.NAME_TAG)
                    .displayname(CC.PRIMARY + name)
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            disguise.setDisplayName(name);
            new DisguiseMenu(disguise).openMenu(player);
        }
    }
}
