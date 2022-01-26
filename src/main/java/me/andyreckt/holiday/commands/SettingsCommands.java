package me.andyreckt.holiday.commands;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.bukkit.entity.Player;

public class SettingsCommands {

    @Command(names = {"toggleprivatemessages", "tpm", "togglepm"}, async = true)
    public static void togglepm(Player sender) {
        Profile profile = Holiday.getInstance().getProfileHandler().getByPlayer(sender);
        profile.setMessagesEnabled(!profile.isMessagesEnabled());

        BasicConfigurationFile messages = Holiday.getInstance().getMessages();

        if (!profile.isMessagesEnabled()) {
            sender.sendMessage(CC.translate(messages.getString("COMMANDS.SETTINGS.PM.OFF")));
        } else {
            sender.sendMessage(CC.translate(messages.getString("COMMANDS.SETTINGS.PM.ON")));
        }
        profile.save();
    }

}
