package me.andyreckt.holiday.bukkit.commands;


import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.tasks.RebootTask;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.core.util.duration.Duration;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ShutdownCommands {

    @Command(names = {"stop"}, permission = Perms.REBOOT, async = true)
    public void stop(CommandSender sender) {
        RebootTask task = Holiday.getInstance().getRebootTask();

        if (task != null && task.isActive()) {
            task.cancel();
        }

        new RebootTask(5000L);
    }


    @Command(names = {"reboot", "restart"}, permission = Perms.REBOOT, async = true)
    public void reboot(CommandSender sender, @Param(name = "cancel/time") String time) {
        RebootTask task = Holiday.getInstance().getRebootTask();

        if (time.equalsIgnoreCase("stop") || time.equalsIgnoreCase("cancel")) {
            if (task != null && task.isActive()) {
                task.cancel();
            }
            Bukkit.broadcastMessage(Locale.REBOOT_CANCELLED.getString());
            return;
        }

        if (task != null && task.isActive()) {
            sender.sendMessage(Locale.SERVER_ALREADY_REBOOTING.getString());
            return;
        }

        Duration duration = Duration.of(time);
        if (duration.isPermanent()) {
            sender.sendMessage(Locale.TIME_FORMAT.getString());
            return;
        }

        new RebootTask(duration.get());
        sender.sendMessage(Locale.REBOOT_STARTED.getString().replace("%time%", duration.toRoundedTime()));
    }

}
