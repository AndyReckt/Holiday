package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.database.redis.packet.MessagePacket;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;
import org.bukkit.Bukkit;

public class MessageSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onMessage(MessagePacket packet) { //TODO w/ SOCIALSPY
        if (Bukkit.getPlayer(packet.getTarget().getUuid()) != null) {
            packet.getTarget().getPlayer().sendMessage(CC.translate("&e(From " + packet.getSender().getDisplayNameWithColor() + "&e) " + packet.getMessage()));
            //MessageCommands.lastMessage.put(packet.getTarget().getUuid(), packet.getSender().getUuid());
        }
    }
}
