package me.andyreckt.holiday.bukkit.server.listeners;

import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.user.punishment.Punishment;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.enums.ChatChannel;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatMute(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
        Punishment punishment = (Punishment) profile.getActivePunishments().stream()
                .filter(punishment1 -> punishment1.getType() == IPunishment.PunishmentType.MUTE)
                .findFirst().orElse(null);
        if (punishment != null) {
            event.setCancelled(true);

            String toSend = "";

            if (punishment.getDurationObject().isPermanent()) {
                toSend = Locale.PUNISHMENT_MUTE_PLAYER.getString();
            } else {
                toSend = Locale.PUNISHMENT_TEMP_MUTE_PLAYER.getString();
            }

            toSend = toSend.replace("%duration%", punishment.getRemainingDuration().getFormatted());

            player.sendMessage(toSend);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChatStaff(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UserProfile profile = (UserProfile) Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        if (profile.getChatChannel() == null) {
            profile.setChatChannel(ChatChannel.GLOBAL);
            Holiday.getInstance().getApi().saveProfile(profile);
            return;
        }

        String playerName = UserConstants.getNameWithColor(profile);
        String server = Holiday.getInstance().getThisServer().getServerName();
        String message = event.getMessage();

        if (profile.getChatChannel() == ChatChannel.STAFF || message.startsWith(Locale.STAFF_CHAT_PREFIX.getRawString())) {
            if (!player.hasPermission(Perms.STAFF_CHAT.get())) {
                profile.setChatChannel(ChatChannel.GLOBAL);
                Holiday.getInstance().getApi().saveProfile(profile);
                return;
            }

            event.setCancelled(true);
            if (message.startsWith(Locale.STAFF_CHAT_PREFIX.getRawString())) {
                message = message.substring(Locale.STAFF_CHAT_PREFIX.getRawString().length());
            }

            String toSend = Locale.STAFF_CHAT.getString()
                    .replace("%player%", playerName)
                    .replace("%server%", server)
                    .replace("%message%", message);
            PacketHandler.send(new BroadcastPacket(
                    toSend,
                    Perms.STAFF_CHAT.get(),
                    AlertType.STAFF_CHAT
            ));
        }

        if (profile.getChatChannel() == ChatChannel.ADMIN || message.startsWith(Locale.ADMIN_CHAT_PREFIX.getRawString())) {
            if (!player.hasPermission(Perms.ADMIN_CHAT.get())) {
                profile.setChatChannel(ChatChannel.GLOBAL);
                Holiday.getInstance().getApi().saveProfile(profile);
                return;
            }

            event.setCancelled(true);
            if (message.startsWith(Locale.ADMIN_CHAT_PREFIX.getRawString())) {
                message = message.substring(Locale.ADMIN_CHAT_PREFIX.getRawString().length());
            }

            String toSend = Locale.ADMIN_CHAT.getString()
                    .replace("%player%", playerName)
                    .replace("%server%", server)
                    .replace("%message%", message);
            PacketHandler.send(new BroadcastPacket(
                    toSend,
                    Perms.ADMIN_CHAT.get(),
                    AlertType.ADMIN_CHAT
            ));
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();

        if (!Holiday.getInstance().getChatManager().isFine(event.getMessage(), event.getPlayer())) {
            event.setCancelled(true);
            return;
        }

        if (!Locale.CHAT_ENABLED.getBoolean()) return;
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        String message = Locale.CHAT_FORMAT.getString()
                .replace("%prefix%", profile.getDisplayRank().getPrefix())
                .replace("%player%", UserConstants.getDisplayNameWithColor(profile))
                .replace("%suffix%", profile.getDisplayRank().getSuffix())
                .replace("%message%", "%2$s");
        event.setFormat(CC.translate(message));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChatCheck(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
        Punishment punishment = (Punishment) profile.getActivePunishments().stream().filter(o -> o.getType().equals(IPunishment.PunishmentType.MUTE)).findFirst().orElse(null);
        if (punishment != null) {
            event.setCancelled(true);

            boolean temp = punishment.getDurationObject().isPermanent();

            player.sendMessage(temp ? Locale.PUNISHMENT_TEMP_MUTE_PLAYER.getString()
                     .replace("%duration%", punishment.getRemainingDuration().getFormatted())
                    : Locale.PUNISHMENT_MUTE_PLAYER.getString());
        }

        if (Holiday.getInstance().getChatManager().isChatMuted()) {
            if (!player.hasPermission(Perms.STAFF_CHAT_BYPASS.get())) {
                event.setCancelled(true);
                player.sendMessage(Locale.CHAT_CURRENTLY_MUTED.getString());
            }
        }
    }


}

