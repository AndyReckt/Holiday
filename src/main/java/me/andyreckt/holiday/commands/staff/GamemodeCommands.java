package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.server.nms.impl.NMS_v1_7;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import me.andyreckt.sunset.annotations.Command;
import me.andyreckt.sunset.annotations.MainCommand;
import me.andyreckt.sunset.annotations.Param;
import me.andyreckt.sunset.annotations.SubCommand;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@MainCommand(names = {"gamemode", "gm"},
        permission = "holiday.gamemode",
        description = "Change your gamemode.",
        usage = "/gamemode <gamemode> [player]",
        helpCommand = "help")
public class GamemodeCommands {


    @SubCommand(names = {"c", "creative", "crea", "1"}, permission = "holiday.gamemode")
    @Command(names = {"gmc", "gm1"}, permission = "holiday.gamemode")
    public static void gmc(CommandSender sender, @Param(name = "target", baseValue = "self") Player target) {
        target.setGameMode(GameMode.CREATIVE);
        sendMessage(sender, target);
    }

    @SubCommand(names = {"a", "adventure", "adv", "2"}, permission = "holiday.gamemode")
    @Command(names = {"gma", "gm2"}, permission = "holiday.gamemode")
    public static void gma(CommandSender sender, @Param(name = "target", baseValue = "self") Player target) {
        target.setGameMode(GameMode.ADVENTURE);
        sendMessage(sender, target);
    }

    @SubCommand(names = {"s", "survival", "surv" , "0"}, permission = "holiday.gamemode")
    @Command(names = {"gms", "gm0"}, permission = "holiday.gamemode")
    public static void gms(CommandSender sender, @Param(name = "target", baseValue = "self") Player target) {
        target.setGameMode(GameMode.SURVIVAL);
        sendMessage(sender, target);
    }

    @SubCommand(names = {"sp", "spectator", "spec", "3"}, permission = "holiday.gamemode")
    @Command(names = {"gmsp", "gm3"}, permission = "holiday.gamemode")
    public static void gmsp(CommandSender sender, @Param(name = "target", baseValue = "self") Player target) {
        if (Holiday.getInstance().getNmsHandler() instanceof NMS_v1_7) {
            sender.sendMessage(CC.RED + "Spectator mode is not supported on 1.7.");
            return;
        }
        target.setGameMode(GameMode.SPECTATOR);
        sendMessage(sender, target);
    }

    @SubCommand(names = {"help"}, permission = "holiday.gamemode")
    public static void gm(CommandSender sender) {
        sender.sendMessage(CC.translate("&cUsage: /gamemode <gamemode> [player]"));
    }

    static void sendMessage(CommandSender sender, Player target) {
        BasicConfigurationFile messages = Holiday.getInstance().getMessages();

        if (sender instanceof Player) {
            if (target == (sender))
                sender.sendMessage(messages.getString("COMMANDS.GAMEMODE.TARGET").replace("<gamemode>", target.getGameMode().name()));
            else {
                target.sendMessage(messages.getString("COMMANDS.GAMEMODE.TARGET").replace("<gamemode>", target.getGameMode().name()));
                sender.sendMessage(messages.getString("COMMANDS.GAMEMODE.SENDER").replace("<gamemode>", target.getGameMode().name()).replace("<target>", target.getName()));
            }
        } else {
            target.sendMessage(messages.getString("COMMANDS.GAMEMODE.TARGET").replace("<gamemode>", target.getGameMode().name()));
            sender.sendMessage(messages.getString("COMMANDS.GAMEMODE.SENDER").replace("<gamemode>", target.getGameMode().name()).replace("<target>", target.getName()));
        }
    }
}