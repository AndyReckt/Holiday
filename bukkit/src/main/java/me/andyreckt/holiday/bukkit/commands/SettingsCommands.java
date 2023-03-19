package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
 
import org.bukkit.entity.Player;

public class SettingsCommands extends BaseCommand {

    @CommandAlias("toggleprivatemessages|tpm|togglepm|togglepms")
    public void togglePm(Player sender) {
        Profile profile = Holiday.getInstance().getApi().getProfile(sender.getUniqueId());
        profile.getSettings().setPrivateMessages(!profile.getSettings().isPrivateMessages());
        Holiday.getInstance().getApi().saveProfile(profile);
        boolean bool = profile.getSettings().isPrivateMessages();
        if (bool) {
            sender.sendMessage(Locale.SETTINGS_PRIVATE_MESSAGE_ON.getString());
        } else {
            sender.sendMessage(Locale.SETTINGS_PRIVATE_MESSAGE_OFF.getString());
        }
    }

    @CommandAlias("toggleprivatemessagessounds|tpms|togglepmsounds|togglepmsound|sounds")
    public void togglePmSounds(Player sender) {
        Profile profile = Holiday.getInstance().getApi().getProfile(sender.getUniqueId());
        profile.getSettings().setPrivateMessagesSounds(!profile.getSettings().isPrivateMessagesSounds());
        Holiday.getInstance().getApi().saveProfile(profile);
        boolean bool = profile.getSettings().isPrivateMessagesSounds();
        if (bool) {
            sender.sendMessage(Locale.SETTINGS_PRIVATE_MESSAGE_SOUNDS_ON.getString());
        } else {
            sender.sendMessage(Locale.SETTINGS_PRIVATE_MESSAGE_SOUNDS_OFF.getString());
        }
    }

}
