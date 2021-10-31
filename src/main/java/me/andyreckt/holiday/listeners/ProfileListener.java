package me.andyreckt.holiday.listeners;

import me.andyreckt.holiday.player.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ProfileListener implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if(Profile.hasProfile(event.getUniqueId())) {
            Profile oldprofile = Profile.getInstance().getFromUUID(event.getUniqueId());
            String newIp = event.getAddress().getHostAddress();
            if(!Objects.equals(newIp, oldprofile.getIp())) {
                oldprofile.setIp(newIp);
                List<String> ips;
                ips = oldprofile.getIps();
                ips.add(newIp);
                oldprofile.setIps(ips);
                oldprofile.save();
            }
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Profile profile = new Profile(event.getPlayer());
        if(!Objects.equals(event.getPlayer().getName(), profile.getName())) {
            profile.setName(event.getPlayer().getName());
            profile.save();
        }
        profile.setLastSeen(new Date());
        profile.setOnline(true);





    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Profile profile = new Profile(event.getPlayer());
        profile.setLastSeen(new Date());
        profile.setOnline(false);
        profile.setFrozen(false);
        profile.save();
    }



}
