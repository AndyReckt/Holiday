package me.andyreckt.holiday.commands;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.sunset.annotations.Command;
import org.bukkit.entity.Player;

public class SettingsCommands {

    @Command(names = {"toggleprivatemessages", "tpm", "togglepm"}, async = true)
    public static void togglepm(Player sender) {
        Profile profile = Holiday.getInstance().getProfileHandler().getByPlayer(sender);
        profile.setMessagesEnabled(!profile.isMessagesEnabled());

        if (!profile.isMessagesEnabled()) {
            sender.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("COMMANDS.SETTINGS.PM.DISABLED")));
        } else {
            sender.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("COMMANDS.SETTINGS.PM.ENABLED")));
        }
        profile.save();
    }

}
