package me.andyreckt.holiday.bungee.listener;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bungee.Bungee;
import me.andyreckt.holiday.bungee.util.Locale;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class StaffSwitchListener implements Listener {

    @EventHandler
    public void onStaffSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Profile profile = Bungee.getInstance().getApi().getProfile(player.getUniqueId());

        if (profile.hasPermission(Locale.STAFF_SWITCH_PERM.getString())) {
            if (event.getFrom() == null) {
                String server = event.getPlayer().getServer().getInfo().getName();
                String playername = Bungee.getInstance().getNameWithColor(profile);
                String toSend = Locale.STAFF_SWITCH_JOIN.getString()
                        .replace("%player%", playername)
                        .replace("%server%", server);
                Bungee.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(toSend, Locale.STAFF_SWITCH_PERM.getString()));
            } else {
                String server = event.getPlayer().getServer().getInfo().getName();
                String oldServer = event.getFrom().getName();
                String playername = Bungee.getInstance().getNameWithColor(profile);
                String toSend = Locale.STAFF_SWITCH_SERVER.getString()
                        .replace("%player%", playername)
                        .replace("%server%", server)
                        .replace("%oldserver%", oldServer);
                Bungee.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(toSend, Locale.STAFF_SWITCH_PERM.getString()));
            }
        }
    }

    @EventHandler
    public void onStaffLeave(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Profile profile = Bungee.getInstance().getApi().getProfile(player.getUniqueId());

        if (profile.hasPermission(Locale.STAFF_SWITCH_PERM.getString())) {
            String server = event.getPlayer().getServer().getInfo().getName();
            String playername = Bungee.getInstance().getNameWithColor(profile);
            String toSend = Locale.STAFF_SWITCH_LEAVE.getString()
                    .replace("%player%", playername)
                    .replace("%server%", server);
            Bungee.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(toSend, Locale.STAFF_SWITCH_PERM.getString()));
        }
    }

}
