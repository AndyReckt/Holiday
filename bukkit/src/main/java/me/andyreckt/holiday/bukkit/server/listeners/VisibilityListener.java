package me.andyreckt.holiday.bukkit.server.listeners;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.other.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class VisibilityListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Holiday.getInstance().getVisibilityHandler().update(event.getPlayer());
        Tasks.runLater(() -> Holiday.getInstance().getVisibilityHandler().update(event.getPlayer()), 30L);
    }

}
