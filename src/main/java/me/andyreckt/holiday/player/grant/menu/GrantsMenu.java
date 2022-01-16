package me.andyreckt.holiday.player.grant.menu;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.grant.Grant;
import me.andyreckt.holiday.player.grant.menu.button.GrantButton;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GrantsMenu extends PaginatedMenu {

    final Profile user;
    final boolean actives;

    public GrantsMenu(Profile user, boolean actives) {
        this.user = user;
        this.actives = actives;
        this.setUpdateAfterClick(true);
    }

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return CC.BLUE + user.getName() + "'s grants";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        Map<Integer, Button> toReturn = new HashMap<>();
        toReturn.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player p0) {
                return new ItemBuilder(Material.PAPER)
                        .displayname(actives ? "&aActive grants" : "&bAll grants")
                        .lore(actives ? "&7&oClick to switch to all grants" : "&7&oClick to switch to actives grants")
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                new GrantsMenu(user, !actives).openMenu(player);
            }
        });

        List<Grant> grants = actives ? user.getActiveGrants() : user.getGrants();

        grants.sort(Comparator.comparingLong(Grant::getExecutedAt));
        Collections.reverse(grants);

        int i = 9;
        for (Grant grant : grants) {
            toReturn.put(i++, new GrantButton(grant));
        }
        return toReturn;
    }
}
