package me.andyreckt.holiday.listeners;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {


    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!Holiday.getInstance().getChatHandler().canChat(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        if (!Holiday.getInstance().getChatHandler().isFine(event.getMessage(), event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        if (!Holiday.getInstance().getSettings().getBoolean("SERVER.CHAT.ENABLED")) return;
        if (event.isCancelled()) return;

        Profile profile = Holiday.getInstance().getProfileHandler().getByPlayer(event.getPlayer());
        String message = Holiday.getInstance().getSettings().getString("SERVER.CHAT.FORMAT")
                .replace("<prefix>", profile.getDisplayRank().getPrefix())
                .replace("<player>", profile.getDisplayNameWithColor())
                .replace("<suffix>", profile.getDisplayRank().getSuffix())
                .replace("<message>", "%2$s");

        event.setFormat(CC.translate(message));
    }

}
