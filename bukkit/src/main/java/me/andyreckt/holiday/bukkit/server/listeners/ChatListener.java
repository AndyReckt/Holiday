package me.andyreckt.holiday.bukkit.server.listeners;

import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
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
        IPunishment punishment = profile.getActivePunishments().stream()
                .filter(punishment1 -> punishment1.getType() == IPunishment.PunishmentType.MUTE)
                .findFirst().orElse(null);
        if (punishment != null) {
            event.setCancelled(true);

            String toSend = "";

            if (punishment.getDuration() == TimeUtil.PERMANENT) {
                toSend = Locale.PUNISHMENT_MUTE_PLAYER.getString();
            } else {
                toSend = Locale.PUNISHMENT_TEMP_MUTE_PLAYER.getString();
            }

            toSend.replace("%duration%", TimeUtil.getDuration(punishment.getDuration()));

            player.sendMessage(toSend);
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
                .replace("%player%", Holiday.getInstance().getDisplayNameWithColor(profile))
                .replace("%suffix%", profile.getDisplayRank().getSuffix())
                .replace("%message%", "%2$s");
        event.setFormat(CC.translate(message));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChatCheck(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
        IPunishment punishment = profile.getActivePunishments().stream().filter(o -> o.getType().equals(IPunishment.PunishmentType.MUTE)).findFirst().orElse(null);
        if (punishment != null) {
            event.setCancelled(true);

            boolean temp = punishment.getDuration() != TimeUtil.PERMANENT;

            player.sendMessage(temp ? Locale.PUNISHMENT_TEMP_MUTE_PLAYER.getString()
                    .replace("%duration%", TimeUtil.getDuration(punishment.getRemainingTime()))
                    : Locale.PUNISHMENT_MUTE_PLAYER.getString());
        }

        if (Holiday.getInstance().getChatManager().isChatMuted()) {
            if (!player.hasPermission(Perms.STAFF_CHAT_BYPASS.get())) {
                event.setCancelled(true);
                player.sendMessage(Locale.CHAT_CURRENTLY_MUTED.getString());
            }
        }
    }

    //TODO: add support for staff chat



}

