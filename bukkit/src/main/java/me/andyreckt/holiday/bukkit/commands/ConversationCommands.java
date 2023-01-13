package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.MessagePacket;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.core.user.UserProfile;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ConversationCommands {

    public static HashMap<UUID, UUID> LAST_MESSAGE = new HashMap<>();

    @Command(names = {"message", "msg", "pm", "tell"}, async = true)
    public void sendMessage(Player player, @Param(name = "target") Profile target, @Param(name = "message", wildcard = true) String message) {

        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
        boolean bypass = profile.isStaff();

        if (target.getUuid().equals(player.getUniqueId())) {
            player.sendMessage(Locale.CANNOT_MESSAGE_YOURSELF.getString());
            return;
        }

        if (!profile.getSettings().isPrivateMessages()) {
            player.sendMessage(Locale.OWN_MESSAGES_DISABLED.getString());
            return;
        }

        if (!target.isOnline()) {
            player.sendMessage(Locale.PLAYER_NOT_ONLINE.getString());
            return;
        }

        if (!target.getSettings().isPrivateMessages() && !bypass) {
            player.sendMessage(Locale.PLAYER_MESSAGES_DISABLED.getString());
            return;
        }

        String toSend = Locale.CONVERSATION_FORMAT_SENT.getString()
                .replace("%player%", UserConstants.getDisplayNameWithColor(target))
                .replace("%message%", message);

        player.sendMessage(toSend);

        Holiday.getInstance().getApi().getRedis().sendPacket(new MessagePacket((UserProfile) target, (UserProfile) profile, message));
        LAST_MESSAGE.put(profile.getUuid(), target.getUuid());
    }

    @Command(names = {"reply", "r"}, async = true)
    public void reply(Player player, @Param(name = "message", wildcard = true) String message) {
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
        boolean bypass = profile.isStaff();


        if (!profile.getSettings().isPrivateMessages()) {
            player.sendMessage(Locale.OWN_MESSAGES_DISABLED.getString());
            return;
        }

        if (!LAST_MESSAGE.containsKey(player.getUniqueId())) {
            player.sendMessage(Locale.NOBODY_TO_REPLY_TO.getString());
            return;
        }

        Profile target = Holiday.getInstance().getApi().getProfile(LAST_MESSAGE.get(player.getUniqueId()));

        if (target.getUuid().equals(player.getUniqueId())) {
            player.sendMessage(Locale.CANNOT_MESSAGE_YOURSELF.getString());
            return;
        }

        if (!target.isOnline()) {
            player.sendMessage(Locale.PLAYER_NOT_ONLINE.getString());
            return;
        }

        if (!target.getSettings().isPrivateMessages() && !bypass) {
            player.sendMessage(Locale.PLAYER_MESSAGES_DISABLED.getString());
            return;
        }

        String toSend = Locale.CONVERSATION_FORMAT_SENT.getString()
                .replace("%player%", UserConstants.getDisplayNameWithColor(target))
                .replace("%message%", message);

        player.sendMessage(toSend);

        Holiday.getInstance().getApi().getRedis().sendPacket(new MessagePacket((UserProfile) target, (UserProfile) profile, message));
        LAST_MESSAGE.put(profile.getUuid(), target.getUuid());
    }

    @Command(names = "socialspy", async = true, permission = Perms.STAFF_SOCIAL_SPY)
    public void spy(Player sender) {
        Profile profile = Holiday.getInstance().getApi().getProfile(sender.getUniqueId());
        profile.getStaffSettings().setSocialSpy(!profile.getStaffSettings().isSocialSpy());

        if (!profile.getStaffSettings().isSocialSpy()) {
            sender.sendMessage(Locale.SETTINGS_STAFF_SOCIAL_SPY_OFF.getString());
        } else {
            sender.sendMessage(Locale.SETTINGS_STAFF_SOCIAL_SPY_ON.getString());
        }
        Holiday.getInstance().getApi().saveProfile(profile);
    }

}
