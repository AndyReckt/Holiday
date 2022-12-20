package me.andyreckt.holiday.bungee.listener;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bungee.Bungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {

    @EventHandler(priority = 64)
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Profile profile = Bungee.getInstance().getApi().getProfile(player.getUniqueId());

        profile.setLastSeen(System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerQuit(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Profile profile = Bungee.getInstance().getApi().getProfile(player.getUniqueId());

        profile.setLastSeen(System.currentTimeMillis());
    }

}
