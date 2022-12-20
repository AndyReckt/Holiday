package me.andyreckt.holiday.bukkit.server.redis.subscriber;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.MessagePacket;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static me.andyreckt.holiday.bukkit.commands.ConversationCommands.LAST_MESSAGE;

public class MessageSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onMessage(MessagePacket packet) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
            if (profile.getStaffSettings().isSocialSpy()) {
                if (!profile.isStaff()) {
                    profile.getStaffSettings().setSocialSpy(false);
                    Holiday.getInstance().getApi().saveProfile(profile);
                    return;
                }
                String toSend = Locale.CONVERSATION_FORMAT_SOCIAL_SPY.getString()
                        .replace("%sender%", Holiday.getInstance().getDisplayNameWithColor(packet.getSender()))
                        .replace("%target%", Holiday.getInstance().getDisplayNameWithColor(packet.getTarget()))
                        .replace("%message%", packet.getMessage());
                player.sendMessage(toSend);
            }
        });

        if (Bukkit.getPlayer(packet.getTarget().getUuid()) == null) return;
        Player player = Bukkit.getPlayer(packet.getTarget().getUuid());

        String toSend = Locale.CONVERSATION_FORMAT_RECEIVED.getString()
                .replace("%player%", Holiday.getInstance().getDisplayNameWithColor(packet.getSender()))
                .replace("%message%", packet.getMessage());

        player.sendMessage(toSend);
        
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        if (profile.getSettings().isPrivateMessagesSounds()) {
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
        }

        LAST_MESSAGE.put(packet.getTarget().getUuid(), packet.getSender().getUuid());
    }
}
