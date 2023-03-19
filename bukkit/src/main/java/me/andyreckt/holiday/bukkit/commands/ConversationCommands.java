package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.MessagePacket;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.user.punishment.Punishment;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ConversationCommands extends BaseCommand { //TODO: filter those

    public static HashMap<UUID, UUID> LAST_MESSAGE = new HashMap<>();

    @CommandAlias("message|msg|pm|tell")
    @Description("Send a private message to a player.")
    @CommandCompletion("@players")
    @Conditions("player")
    public void sendMessage(CommandSender sender, @Single @Name("target") Profile target, @Name("message") String message) {
        Player player = (Player) sender;
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
        boolean bypass = profile.isStaff();

        if (profile.isMuted()) {
            Punishment punishment = (Punishment) profile.getActivePunishments().stream()
                    .filter(punishment1 -> punishment1.getType() == IPunishment.PunishmentType.MUTE)
                    .findFirst().orElse(null);

            String toSend = "";
            if (punishment.getDurationObject().isPermanent()) {
                toSend = Locale.PUNISHMENT_MUTE_PLAYER.getString();
            } else {
                toSend = Locale.PUNISHMENT_TEMP_MUTE_PLAYER.getString();
            }

            toSend = toSend.replace("%duration%", punishment.getRemainingDuration().getFormatted());
            player.sendMessage(toSend);
            return;
        }

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

        PacketHandler.send(new MessagePacket((UserProfile) target, (UserProfile) profile, message));
        LAST_MESSAGE.put(profile.getUuid(), target.getUuid());
    }

    @CommandAlias("reply|r")
    @Description("Reply to the last player you messaged.")
    @Conditions("player")
    public void reply(CommandSender sender, @Name("message") String message) {
        Player player = (Player) sender;
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
        boolean bypass = profile.isStaff();

        if (profile.isMuted()) {
            Punishment punishment = (Punishment) profile.getActivePunishments().stream()
                    .filter(punishment1 -> punishment1.getType() == IPunishment.PunishmentType.MUTE)
                    .findFirst().orElse(null);

            String toSend = "";
            if (punishment.getDurationObject().isPermanent()) {
                toSend = Locale.PUNISHMENT_MUTE_PLAYER.getString();
            } else {
                toSend = Locale.PUNISHMENT_TEMP_MUTE_PLAYER.getString();
            }

            toSend = toSend.replace("%duration%", punishment.getRemainingDuration().getFormatted());
            player.sendMessage(toSend);
            return;
        }

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

        PacketHandler.send(new MessagePacket((UserProfile) target, (UserProfile) profile, message));
        LAST_MESSAGE.put(profile.getUuid(), target.getUuid());
    }

    @CommandAlias("socialspy|sspy")
    @Description("Toggle social spy on or off.")
    @CommandPermission("core.staff.socialspy")
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
