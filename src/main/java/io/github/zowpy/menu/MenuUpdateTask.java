package io.github.zowpy.menu;

import org.bukkit.scheduler.*;
import org.bukkit.plugin.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.entity.*;

public class MenuUpdateTask extends BukkitRunnable
{
    public MenuUpdateTask(final Plugin plugin) {
        this.runTaskTimerAsynchronously(plugin, 2L, 2L);
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
