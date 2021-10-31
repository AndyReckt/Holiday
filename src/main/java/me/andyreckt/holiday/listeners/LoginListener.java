package me.andyreckt.holiday.listeners;

import me.andyreckt.holiday.player.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.List;
import java.util.Objects;

public class LoginListener implements Listener {

    @EventHandler
    public void onPreLoginIpAdd(AsyncPlayerPreLoginEvent event) {
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
    public void onLoginProfileCreate(PlayerLoginEvent event) {
        Profile profile = new Profile(event.getPlayer());


    }
}
