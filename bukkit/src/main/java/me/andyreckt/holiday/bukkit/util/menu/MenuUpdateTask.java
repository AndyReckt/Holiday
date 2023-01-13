package me.andyreckt.holiday.bukkit.util.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class MenuUpdateTask extends BukkitRunnable {
    public MenuUpdateTask(final Plugin plugin) {
        this.runTaskTimerAsynchronously(plugin, 20, 20);
    }

    public void run() {
        for (final Map.Entry<UUID, Menu> entry : Menu.getCurrentlyOpenedMenus().entrySet()) {
            final UUID key = entry.getKey();
            final Menu value = entry.getValue();
            final Player player = Bukkit.getPlayer(key);
            if (player != null && value.isAutoUpdate()) {
                value.openMenu(player);
            }
        }
    }
}
