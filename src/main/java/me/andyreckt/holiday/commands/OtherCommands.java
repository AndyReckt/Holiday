package me.andyreckt.holiday.commands;

import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.command.Command;
import org.bukkit.command.CommandSender;

public class OtherCommands {

    @Command(names = {"garbage"}, perm = "holiday.garbage", async = true)
    public static void gc(CommandSender sender) {
        System.gc();
        sender.sendMessage(CC.translate("&aSuccessfully ran the garbage collector."));
    }

}
