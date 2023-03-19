package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Single;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.menu.punishments.PunishmentLookupMenu;
import me.andyreckt.holiday.bukkit.server.menu.staff.StaffAlertsMenu;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
 
  
import org.bukkit.entity.Player;

public class StaffCommands extends BaseCommand {

    @CommandAlias("staffalerts|sa")
    @CommandPermission("core.staff.notifications")
    public void staffAlerts(Player sender) {
        new StaffAlertsMenu().openMenu(sender);
    }

    @CommandAlias("lookupid|checkid")
    @CommandPermission("core.command.checkpunishments")
    public void checkId(Player sender, @Single @Name("id") String id) {
        IPunishment punishment = Holiday.getInstance().getApi().getPunishment(id);

        if (punishment == null) {
            sender.sendMessage(Locale.PUNISHMENT_NOT_FOUND.getString());
            return;
        }

        new PunishmentLookupMenu(punishment).openMenu(sender);
    }

}
