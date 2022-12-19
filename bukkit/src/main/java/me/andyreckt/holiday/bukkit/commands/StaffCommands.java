package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.bukkit.server.menu.staff.StaffAlertsMenu;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import org.bukkit.entity.Player;

public class StaffCommands {

    @Command(names = "staffalerts", permission = Perms.STAFF_VIEW_NOTIFICATIONS, description = "Toggle your staff alerts.")
    public void staffAlerts(Player sender) {
        new StaffAlertsMenu().openMenu(sender);
    }

}
