package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.menu.punishments.PunishmentLookupMenu;
import me.andyreckt.holiday.bukkit.server.menu.staff.StaffAlertsMenu;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffCommands extends BaseCommand {

    @CommandAlias("staffalerts|sa")
    @CommandPermission("core.staff.notifications")
    @Conditions("player")
    public void staffAlerts(CommandSender sen) {
        Player sender = (Player) sen;
        new StaffAlertsMenu().openMenu(sender);
    }

    @CommandAlias("lookupid|checkid")
    @CommandPermission("core.command.checkpunishments")
    @Conditions("player")
    public void checkId(CommandSender sen, @Single @Name("id") String id) {
        Player sender = (Player) sen;
        IPunishment punishment = Holiday.getInstance().getApi().getPunishment(id);

        if (punishment == null) {
            sender.sendMessage(Locale.PUNISHMENT_NOT_FOUND.getString());
            return;
        }

        new PunishmentLookupMenu(punishment).openMenu(sender);
    }

}
