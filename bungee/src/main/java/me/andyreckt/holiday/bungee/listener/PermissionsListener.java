package me.andyreckt.holiday.bungee.listener;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bungee.Bungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PermissionsListener implements Listener {

    @EventHandler
    public void onPermission(PermissionCheckEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) return;

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        Profile profile = Bungee.getInstance().getApi().getProfile(player.getUniqueId());

        if (profile == null) return;

        event.setHasPermission(profile.hasPermission(event.getPermission()));
    }

}
