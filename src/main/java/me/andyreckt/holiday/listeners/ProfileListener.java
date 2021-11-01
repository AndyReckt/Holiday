package me.andyreckt.holiday.listeners;

import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.punishments.PunishData;
import me.andyreckt.holiday.punishments.Punishment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;


public class ProfileListener implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if(Profile.hasProfile(event.getUniqueId())) {
            Profile oldprofile = Profile.getFromUUID(event.getUniqueId());
            String newIp = event.getAddress().getHostAddress();
            if(!newIp.equalsIgnoreCase(oldprofile.getIp())) {
                oldprofile.setIp(newIp);
                oldprofile.getIps().add(newIp);
                oldprofile.save();
            }
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Profile profile = Profile.getFromPlayer(event.getPlayer());
        if(!event.getPlayer().getName().equalsIgnoreCase(profile.getName())) {
            profile.setName(event.getPlayer().getName());
            profile.save();
        }
        profile.setLastSeen(new Date());
        profile.setOnline(true);

        AtomicBoolean allowed = new AtomicBoolean(true);

        Punishment.getAllPunishments(profile).forEach(punish -> {
            PunishData data = PunishData.getFromDocument(punish);
            if(data.isActive()) {
                switch (data.getType()){
                    case TEMP_BAN: allowed.set(false);
                    case BAN: allowed.set(false);
                    case IP_BAN: allowed.set(false);
                    case BLACKLIST: allowed.set(false);
                }
            }
        });




    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Profile profile = Profile.getFromPlayer(event.getPlayer());
        profile.setLastSeen(new Date());
        profile.setOnline(false);
        profile.setFrozen(false);
        profile.save();

        Profile.profileCache.remove(event.getPlayer().getUniqueId());
    }



}
