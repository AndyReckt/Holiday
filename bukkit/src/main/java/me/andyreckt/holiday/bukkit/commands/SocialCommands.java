package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import org.bukkit.command.CommandSender;

public class SocialCommands {


    @Command(names = {"discord", "disc", "dc"}, async = true)
    public void discord(CommandSender sender) {
        sender.sendMessage(Locale.SOCIAL_DISCORD.getStringNetwork());
    }

    @Command(names = {"teamspeak", "ts"})
    public void ts(CommandSender sender) {
        sender.sendMessage(Locale.SOCIAL_TEAMSPEAK.getStringNetwork());
    }

    @Command(names = {"twitter"})
    public void twitter(CommandSender sender) {
        sender.sendMessage(Locale.SOCIAL_TWITTER.getStringNetwork());
    }

    @Command(names = {"store", "shop"})
    public void store(CommandSender sender) {
        sender.sendMessage(Locale.SOCIAL_STORE.getStringNetwork());
    }


}
