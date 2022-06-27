package me.andyreckt.holiday.commands;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.MessagePacket;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ConversationCommands {

    public static HashMap<UUID, UUID> lastMessage = new HashMap<>();

    @Command(names = {"message", "msg", "pm", "tell"}, async = true)
    public static void sendMessage(Player player, @Param(name = "target") Profile target, @Param(name = "message", wildcard = true) String message) {

        Profile profile = Holiday.getInstance().getProfileHandler().getByPlayer(player);
        boolean bypass = profile.isStaff();

        if (target.getUuid().equals(player.getUniqueId())) {
            player.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.YOURSELF"));
            return;
        }

        if (!profile.isMessagesEnabled()) {
            player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.TOGGLED")));
            return;
        }

        if (!target.isOnline()) {
            player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.TARGETNOTONLINE")));
            return;
        }

        if (!target.isMessagesEnabled() && !bypass) {
            player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.TARGETTOGGLED")));
            return;
        }

        player.sendMessage(CC.translate(
                Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.FORMAT.SENT")
                        .replace("<player>", target.getDisplayNameWithColor())
                        .replace("<message>", message)
        ));

        Holiday.getInstance().getRedis().sendPacket(new MessagePacket(target, profile, message));
        lastMessage.put(profile.getUuid(), target.getUuid());
    }

    @Command(names = {"reply", "r"}, async = true)
    public static void reply(Player player, @Param(name = "message", wildcard = true) String message) {

        ProfileHandler ph = Holiday.getInstance().getProfileHandler();

        Profile profile = ph.getByPlayer(player);
        boolean bypass = profile.isStaff();


        if (!profile.isMessagesEnabled()) {
            player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.TOGGLED")));
            return;
        }

        if (!lastMessage.containsKey(player.getUniqueId())) {
            player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.NOBODYTOREPLY")));
            return;
        }

        Profile target = ph.getByUUID(lastMessage.get(player.getUniqueId()));

        if (target.getUuid().equals(player.getUniqueId())) {
            player.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.YOURSELF"));
            return;
        }

        if (!target.isOnline()) {
            player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.TARGETNOTONLINE")));
            return;
        }

        if (!target.isMessagesEnabled() &&!bypass) {
            player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.TARGETTOGGLED")));
            return;
        }

        player.sendMessage(CC.translate(
                Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.FORMAT.SENT")
                        .replace("<player>", target.getDisplayNameWithColor())
                        .replace("<message>", message)
        ));

        Holiday.getInstance().getRedis().sendPacket(new MessagePacket(target, profile, message));
        lastMessage.put(profile.getUuid(), target.getUuid());
    }

    @Command(names = "socialspy", async = true, perm = "holiday.socialspy")
    public static void spy(Player sender) {
        Profile profile = Holiday.getInstance().getProfileHandler().getByUUID(sender.getUniqueId());
        profile.setSocialSpy(!profile.isSocialSpy());

        if (!profile.isSocialSpy()) {
            sender.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.SOCIALSPY.DISABLED")));
        } else {
            sender.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.SOCIALSPY.ENABLED")));
        }
        profile.save();
    }

}
