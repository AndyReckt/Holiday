package me.andyreckt.holiday.bukkit.server.redis.packet;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.core.user.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static me.andyreckt.holiday.bukkit.commands.ConversationCommands.LAST_MESSAGE;

@Getter
@AllArgsConstructor
public class MessagePacket implements Packet {

    private final UserProfile target, sender;
    private final String message;

    @Override
    public void onReceive() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
            if (profile.getStaffSettings().isSocialSpy()) {
                if (!profile.isStaff()) {
                    profile.getStaffSettings().setSocialSpy(false);
                    Holiday.getInstance().getApi().saveProfile(profile);
                    return;
                }
                String toSend = Locale.CONVERSATION_FORMAT_SOCIAL_SPY.getString()
                        .replace("%sender%", UserConstants.getDisplayNameWithColor(sender))
                        .replace("%target%", UserConstants.getDisplayNameWithColor(target))
                        .replace("%message%", message);
                player.sendMessage(toSend);
            }
        });

        if (Bukkit.getPlayer(target.getUuid()) == null) return;
        Player player = Bukkit.getPlayer(target.getUuid());

        String toSend = Locale.CONVERSATION_FORMAT_RECEIVED.getString()
                .replace("%player%", UserConstants.getDisplayNameWithColor(sender))
                .replace("%message%", message);

        player.sendMessage(toSend);

        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        if (profile.getSettings().isPrivateMessagesSounds()) {
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
        }

        LAST_MESSAGE.put(target.getUuid(), sender.getUuid());
    }
}