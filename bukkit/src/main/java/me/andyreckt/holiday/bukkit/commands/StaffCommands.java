package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.menu.punishments.PunishmentLookupMenu;
import me.andyreckt.holiday.bukkit.server.menu.staff.StaffAlertsMenu;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import org.bukkit.entity.Player;

public class StaffCommands {

    @Command(names = "staffalerts", permission = Perms.STAFF_VIEW_NOTIFICATIONS, description = "Toggle your staff alerts.")
    public void staffAlerts(Player sender) {
        new StaffAlertsMenu().openMenu(sender);
    }

    @Command(names = {"lookupid", "checkid"}, permission = Perms.CHECK_PUNISHMENTS, description = "Lookup a punishment by it's ID.")
    public void checkId(Player sender, @Param(name = "id") String id) {
        IPunishment punishment = Holiday.getInstance().getApi().getPunishment(id);

        if (punishment == null) {
            sender.sendMessage(Locale.PUNISHMENT_NOT_FOUND.getString());
            return;
        }

        new PunishmentLookupMenu(punishment).openMenu(sender);
    }

}
