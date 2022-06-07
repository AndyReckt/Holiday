package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportCommands {

    @Command(names = {"tpall"}, perm = "holiday.tpall")
    public static void tpall(Player sender) {
        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(sender));
        sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.TELEPORT.TPALL"));
    }
    @Command(names = {"tp", "teleport"}, perm = "holiday.teleport")
    public static void tp(Player sender, @Param(name = "player") Player target) {
        sender.teleport(target.getLocation());
        sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.TELEPORT.TP").replace("<player>", target.getName()));
    }
    @Command(names = {"tph", "tphere", "s"}, perm = "holiday.teleporthere")
    public static void tphere(Player sender, @Param(name = "player") Player target) {
        target.teleport(sender.getLocation());
        sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.TELEPORT.TPHERE").replace("<player>", target.getName()));
    }

    @Command(names = {"tppos", "teleportpos"}, perm = "holiday.tppos")
    public static void tpPos(Player sender, @Param(name = "x") double x, @Param(name = "y") double y, @Param(name = "z") double z) {
        if (x > 3000000 || y > 260 || z > 3000000 || x < -3000000 || y < -10 || z < -3000000) {
            sender.sendMessage(CC.translate("&cMaximal coordinates are +/- x: 3000000 y: 260/-10 z: 3000000"));
            return;
        }
        Location location = new Location(sender.getWorld(), x, y, z, sender.getLocation().getYaw(), sender.getLocation().getPitch());
        sender.teleport(location);
        sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.TELEPORT.TPPOS").replace("<pos>", (x + " " + y + " " + z)));
    }

}
