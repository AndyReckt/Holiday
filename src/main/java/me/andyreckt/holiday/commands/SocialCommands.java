package me.andyreckt.holiday.commands;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.StringUtil;
import me.andyreckt.holiday.utils.command.Command;
import org.bukkit.command.CommandSender;

public class SocialCommands {


    @Command(names = {"discord", "disc", "dc"})
    public static void discord(CommandSender sender) {
        sender.sendMessage(CC.translate(
                StringUtil.addNetworkPlaceholder(Holiday.getInstance().getMessages().getString("COMMANDS.SOCIAL.DISCORD"))));
    }
    @Command(names = {"teamspeak", "ts"})
    public static void ts(CommandSender sender) {
        sender.sendMessage(CC.translate(
                StringUtil.addNetworkPlaceholder(Holiday.getInstance().getMessages().getString("COMMANDS.SOCIAL.TEAMSPEAK"))));
    }
    @Command(names = {"twitter"})
    public static void twitter(CommandSender sender) {
        sender.sendMessage(CC.translate(
                StringUtil.addNetworkPlaceholder(Holiday.getInstance().getMessages().getString("COMMANDS.SOCIAL.TWITTER"))));
    }
    @Command(names = {"store", "shop"})
    public static void store(CommandSender sender) {
        sender.sendMessage(CC.translate(
                StringUtil.addNetworkPlaceholder(Holiday.getInstance().getMessages().getString("COMMANDS.SOCIAL.STORE"))));
    }


}
