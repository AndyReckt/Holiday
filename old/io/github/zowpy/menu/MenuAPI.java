package io.github.zowpy.menu;

import org.bukkit.plugin.*;
import org.bukkit.event.*;

public final class MenuAPI
{
    public MenuAPI(final Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new ButtonListener(plugin), plugin);
        new MenuUpdateTask(plugin);
    }
}
