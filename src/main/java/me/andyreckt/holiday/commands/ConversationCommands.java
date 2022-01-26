package me.andyreckt.holiday.commands;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.MessagePacket;
import me.andyreckt.holiday.player.Profile;
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

        BasicConfigurationFile messages = Holiday.getInstance().getMessages();

        Profile profile = Holiday.getInstance().getProfileHandler().getByPlayer(player);
        boolean bypass = profile.isStaff();

        if (!profile.isMessagesEnabled()) {
            player.sendMessage(CC.translate(messages.getString("COMMANDS.CONVERSATION.TOGGLED")));
            return;
        }

        if (!target.isOnline()) {
            player.sendMessage(CC.translate(messages.getString("COMMANDS.CONVERSATION.TARGETNOTONLINE")));
            return;
        }

        if (!target.isMessagesEnabled() && !bypass) {
            player.sendMessage(CC.translate(messages.getString("COMMANDS.CONVERSATION.TARGETTOGGLED")));
            return;
        }

        player.sendMessage(CC.translate(
                messages.getString("COMMANDS.CONVERSATION.FORMAT.SENT")
                        .replace("<player>", target.getDisplayNameWithColor())
                        .replace("<message>", message)
        ));

        Holiday.getInstance().getRedis().sendPacket(new MessagePacket(target, profile, message));
        lastMessage.put(profile.getUuid(), target.getUuid());
    }

}
