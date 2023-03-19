package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import me.andyreckt.holiday.bukkit.util.files.Locale;
 
import org.bukkit.command.CommandSender;

public class SocialCommands extends BaseCommand {


    @CommandAlias("discord|disc|dc")
    public void discord(CommandSender sender) {
        sender.sendMessage(Locale.SOCIAL_DISCORD.getStringNetwork());
    }

    @CommandAlias("teamspeak|ts")
    public void ts(CommandSender sender) {
        sender.sendMessage(Locale.SOCIAL_TEAMSPEAK.getStringNetwork());
    }

    @CommandAlias("twitter")
    public void twitter(CommandSender sender) {
        sender.sendMessage(Locale.SOCIAL_TWITTER.getStringNetwork());
    }

    @CommandAlias("store|shop")
    public void store(CommandSender sender) {
        sender.sendMessage(Locale.SOCIAL_STORE.getStringNetwork());
    }


}
