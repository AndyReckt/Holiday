package me.andyreckt.holiday.bukkit.util.uuid;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class UUIDListener implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        HolidayUUIDCache.update(event.getUniqueId(), event.getName());
    }

}

