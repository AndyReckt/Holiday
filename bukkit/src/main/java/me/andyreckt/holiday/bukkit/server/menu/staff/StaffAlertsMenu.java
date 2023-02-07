package me.andyreckt.holiday.bukkit.server.menu.staff;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.settings.StaffAlerts;
import me.andyreckt.holiday.core.util.enums.AlertType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class StaffAlertsMenu extends PaginatedMenu {

    public StaffAlertsMenu() {
        this.setUpdateAfterClick(true);
        this.setGlassColor(10);
    }

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return "Staff Alerts";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        for (AlertType value : AlertType.values()) {
            if (value.isAdmin()) continue;
            buttons.put(buttons.size(), new StaffAlertsButton(value, profile));
        }

        for (AlertType value : AlertType.values()) {
            if (!value.isAdmin()) continue;
            if (!player.hasPermission(Perms.ADMIN_VIEW_NOTIFICATIONS.get())) continue;
            buttons.put(buttons.size(), new StaffAlertsButton(value, profile));
        }

        return buttons;
    }

    static class StaffAlertsButton extends Button {

        private final AlertType alert;
        private final Profile profile;

        public StaffAlertsButton(AlertType alert, Profile profile) {
            this.profile = profile;
            this.alert = alert;
        }

        @Override
        public ItemStack getButtonItem(Player p0) {
            boolean bool = alert.isAlerts(profile);
            return new ItemBuilder(Material.NOTE_BLOCK)
                    .displayname(CC.SECONDARY + alert.getName())
                    .lore("",
                    bool    ? CC.B_PRIMARY + CC.LINE + " " + CC.GREEN + "Enabled"
                            : CC.B_PRIMARY + CC.LINE + " " + CC.WHITE + "Enabled",
                    !bool   ? CC.B_PRIMARY + CC.LINE + " " + CC.RED + "Disabled"
                            : CC.B_PRIMARY + CC.LINE + " " + CC.WHITE + "Disabled"
            ).build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            alert.setAlerts(profile, !alert.isAlerts(profile));
        }
    }



}
