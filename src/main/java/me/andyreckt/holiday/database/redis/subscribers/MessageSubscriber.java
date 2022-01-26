package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.commands.ConversationCommands;
import me.andyreckt.holiday.database.redis.packet.MessagePacket;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;
import org.bukkit.Bukkit;

public class MessageSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onMessage(MessagePacket packet) { //TODO w/ SOCIALSPY
        if (Bukkit.getPlayer(packet.getTarget().getUuid()) == null) return;
        packet.getTarget().getPlayer().sendMessage(CC.translate(
                Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.FORMAT.RECEIVED")
                        .replace("<player>", packet.getSender().getDisplayNameWithColor())
                        .replace("<message>", packet.getMessage()
                        )));
        ConversationCommands.lastMessage.put(packet.getTarget().getUuid(), packet.getSender().getUuid());

        Holiday.getInstance().getProfileHandler().getOnlineProfiles().forEach(profile -> {
            if (!profile.isSocialSpy()) return;
            if (!profile.isOnline()) return;
            if (profile.getPlayer() == null) return;
            profile.getPlayer().sendMessage(CC.translate(
                    Holiday.getInstance().getMessages().getString("COMMANDS.CONVERSATION.FORMAT.SOCIALSPY")
                            .replace("<sender>", packet.getSender().getDisplayNameWithColor())
                            .replace("<target>", packet.getTarget().getDisplayNameWithColor())
                            .replace("<message>", packet.getMessage()
                            )));
        });
    }
}
