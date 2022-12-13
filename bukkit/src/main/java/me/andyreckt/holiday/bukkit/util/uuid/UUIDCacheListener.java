package me.andyreckt.holiday.bukkit.util.uuid;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.Logger;
import me.andyreckt.holiday.bukkit.util.text.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class UUIDCacheListener implements Listener {

    private final Holiday plugin;

    public UUIDCacheListener(Holiday plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        String foundName = this.plugin.getUuidCache().name(player.getUniqueId());

        if (foundName == null) {
            this.plugin.getUuidCache().update(player.getUniqueId(), player.getName());
            Logger.log(CC.translate("&9[UUID Cache] &b" + player.getName() + "'s &bname and uuid have been updated to the UUID Cache."));
            return;
        }

        if (foundName.equalsIgnoreCase(player.getName())) {
            return;
        }

        this.plugin.getUuidCache().update(player.getUniqueId(), player.getName());
        Logger.log(CC.translate("&9[UUID Cache] &b" + player.getName() + " 's &bname and uuid have been updated to the UUID Cache."));
    }

}
