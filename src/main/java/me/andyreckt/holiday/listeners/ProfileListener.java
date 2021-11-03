package me.andyreckt.holiday.listeners;

import me.andyreckt.holiday.Files;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.punishments.PunishData;
import me.andyreckt.holiday.punishments.Punishment;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.StringUtil;
import me.andyreckt.holiday.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;


public class ProfileListener implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if(Profile.hasProfile(event.getUniqueId())) {
            Profile oldprofile = Profile.getFromUUIDWithoutCache(event.getUniqueId());
            Tasks.runLater(() -> {
                String newIp = event.getAddress().getHostAddress();
                if(!newIp.equalsIgnoreCase(oldprofile.getIp())) {
                    oldprofile.setIp(newIp);
                    oldprofile.getIps().add(newIp);
                    oldprofile.save();
                }
            }, 20L);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Profile profile = Profile.getFromUUID(event.getPlayer().getUniqueId());

        profile.setLastSeen(new Date());
        profile.setOnline(true);

        AtomicReference<String> message = new AtomicReference<>("");
        AtomicReference<String> alertMessage = new AtomicReference<>("");
        AtomicReference<PunishData> activePunishment = new AtomicReference<>(null);

        Punishment.getAllPunishments(profile).forEach(punish -> {
            PunishData data = PunishData.getFromDocument(punish);
            if(data.isActive()) {
                switch (data.getType()){
                    case TEMP_BAN: {
                        message.set(Files.Messages.TEMP_BAN_JOIN.getString());
                        alertMessage.set(Files.Messages.NOTIFY_BANNED.getString());
                        activePunishment.set(data);
                    }
                    case BAN: {
                        message.set(Files.Messages.BAN_JOIN.getString());
                        alertMessage.set(Files.Messages.NOTIFY_BANNED.getString());
                        activePunishment.set(data);
                    }
                    case IP_BAN: {
                        message.set(Files.Messages.IP_BAN_JOIN.getString());
                        alertMessage.set(Files.Messages.NOTIFY_BANNED.getString());
                        activePunishment.set(data);
                    }
                    case BLACKLIST: {
                        message.set(Files.Messages.BLACKLIST_JOIN.getString());
                        alertMessage.set(Files.Messages.NOTIFY_BANNED.getString());
                        activePunishment.set(data);
                    }
                }
            }
        });

        if(activePunishment.get() != null) {
            PunishData data = activePunishment.get();
            String string = CC.translate(message.get());
            String alert = CC.translate(alertMessage.get());
            string = StringUtil.addNetworkPlaceholder(string);
            string = string.replace("<executor>", data.getAddedBy().getName());
            string = string.replace("<reason>", data.getAddedReason());
            string = string.replace("<duration>", data.getNiceDuration());
            string = string.replace("<executor>", data.getAddedBy().getName());
            alert =  alert.replace("<player>", data.getPunished().getName());
            CC.sendMessageToAdmins(alert);
            event.getPlayer().kickPlayer(string);
            return;
        }

        Punishment.getAllPunishments(profile.getIp()).forEach(punish -> {

            Player player = null;
            PermissionAttachment attachment = player.addAttachment(Holiday.getInstance());
            attachment.setPermission("perm",true);

            PunishData data = PunishData.getFromDocument(punish);
            if(data.isActive()) {
                switch (data.getType()) {
                    case TEMP_BAN: {
                        alertMessage.set(Files.Messages.NOTIFY_ALT.getString());
                        String alert = alertMessage.get().replace("<alt>", data.getPunished().getName());
                        alert =  alert.replace("<player>", event.getPlayer().getName());
                        CC.sendMessageToAdmins(alert);
                    }
                    case BAN: {
                        alertMessage.set(Files.Messages.NOTIFY_ALT.getString());
                        String alert = alertMessage.get().replace("<alt>", data.getPunished().getName());
                        alert =  alert.replace("<player>", event.getPlayer().getName());
                        CC.sendMessageToAdmins(alert);
                    }
                    case IP_BAN: {
                        message.set(Files.Messages.IP_BAN_JOIN.getString());
                        alertMessage.set(Files.Messages.NOTIFY_BANNED.getString());
                        activePunishment.set(data);
                    }
                    case BLACKLIST: {
                        message.set(Files.Messages.BLACKLIST_JOIN.getString());
                        alertMessage.set(Files.Messages.NOTIFY_BANNED.getString());
                        activePunishment.set(data);
                    }
                }
            }
        });

        if(activePunishment.get() != null) {
            PunishData data = activePunishment.get();
            String string = message.get();
            string = StringUtil.addNetworkPlaceholder(string);
            string = string.replace("<executor>", data.getAddedBy().getName());
            string = string.replace("<reason>", data.getAddedReason());
            string = string.replace("<duration>", data.getNiceDuration());
            string = string.replace("<executor>", data.getAddedBy().getName());
            String alert = alertMessage.get().replace("<alt>", data.getPunished().getName());
            alert =  alert.replace("<player>", event.getPlayer().getName());
            CC.sendMessageToAdmins(alert);
            event.getPlayer().kickPlayer(string);
            return;
        }





    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Profile profile = Profile.getFromPlayer(event.getPlayer());
        profile.setLastSeen(new Date());
        profile.setOnline(false);
        profile.setFrozen(false);
        profile.save();

        Profile.profileCache.remove(event.getPlayer().getUniqueId());

        Tasks.runAsyncLater(() -> {
            Profile staffChangeServer = Profile.getFromUUIDWithoutCache(event.getPlayer().getUniqueId());
            if(staffChangeServer.isOnline() && staffChangeServer.getHighestRank().isStaff()){

            }
        }, 20L);

    }






}
