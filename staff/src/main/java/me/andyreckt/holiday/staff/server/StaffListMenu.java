package me.andyreckt.holiday.staff.server;

import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.bukkit.util.text.CC;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StaffListMenu extends PaginatedMenu {
    @Override
    public String getPrePaginatedTitle(Player p0) {
        return CC.SECONDARY + "Staff List";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        Map<Integer, Button> buttons = new HashMap<>();
        API api = Holiday.getInstance().getApi();
        Set<Profile> players = api.getOnlinePlayers().keySet().stream()
                .map(api::getProfile)
                .filter(Profile::isStaff)
                .collect(Collectors.toSet());
        players.forEach(player -> buttons.put(buttons.size(), new StaffListButton(player)));
        return buttons;
    }

    private static class StaffListButton extends Button {
        private final Profile profile;

        public StaffListButton(Profile player) {
            this.profile = player;
        }

        @Override
        public ItemStack getButtonItem(Player p0) {
            return new ItemBuilder(Material.SKULL_ITEM)
                    .durability(SkullType.PLAYER.ordinal())
                    .owner(profile.getName())
                    .displayname(UserConstants.getNameWithColor(profile))
                    .lore(CC.MENU_BAR,
                            CC.CHAT + "StaffMode: " + yesNo(profile.getStaffSettings().isStaffMode()),
                            CC.CHAT + "Vanished: " + yesNo(profile.getStaffSettings().isVanished()),
                            CC.MENU_BAR)
                    .build();
        }

        private String yesNo(boolean bool) {
            return bool ? "&aYes" : "&cNo";
        }
    }
}
