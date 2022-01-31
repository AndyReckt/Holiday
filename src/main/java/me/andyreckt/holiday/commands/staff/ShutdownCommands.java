package me.andyreckt.holiday.commands.staff;


import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.server.reboot.RebootTask;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.TimeUtil;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ShutdownCommands {

    @Command(names = {"stop"},  perm = "holiday.reboot", async = true)
    public static void stop(CommandSender sender) {
        RebootTask task = Holiday.getInstance().getRebootTask();

        if (task != null && task.isActive()) {
            task.cancel();
        }

        new RebootTask(5000L);
    }


    @Command(names = {"reboot", "restart"},  perm = "holiday.reboot", async = true)
    public static void reboot(CommandSender sender, @Param(name = "cancel/time") String time) {
        BasicConfigurationFile messages = Holiday.getInstance().getMessages();
        RebootTask task = Holiday.getInstance().getRebootTask();

        if (time.equalsIgnoreCase("stop") || time.equalsIgnoreCase("cancel")) {
            if (task != null && task.isActive()) {
                task.cancel();
            }
            Bukkit.broadcastMessage(messages.getString("REBOOT.CANCELLED"));
            return;
        }

        if (task != null && task.isActive()) {
            sender.sendMessage(messages.getString("REBOOT.ANOTHER"));
            return;
        }

        long millis = TimeUtil.getDuration(time);
        if (millis == -1L) {
            sender.sendMessage(CC.translate("&cThis is not a valid duration."));
            return;
        }

        new RebootTask(millis);
        sender.sendMessage(messages.getString("REBOOT.STARTED").replace("<time>", time));
    }

}
