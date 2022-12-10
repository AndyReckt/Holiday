package me.andyreckt.holiday.bukkit.server.listeners;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.util.text.HashUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Objects;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLoginUserProfile(AsyncPlayerPreLoginEvent event) {

        UserProfile profile = (UserProfile) Holiday.getInstance().getApi().getProfile(event.getUniqueId());
        if (!profile.getName().equalsIgnoreCase(event.getName())) {
            profile.setName(event.getName());
        }

        if (!Objects.equals(profile.getIp(), HashUtils.hash(event.getAddress().getHostAddress()))) {
            profile.addNewCurrentIP(event.getAddress().getHostAddress());
        }

        Holiday.getInstance().getApi().saveProfile(profile);
    }


    //TODO: Punishment system
//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onLoginPunishments(PlayerLoginEvent event) {
//        Player player = event.getPlayer();
//        Punishment punishment = Holiday.getInstance().getApi().getPunishments(player.getUniqueId()).stream()
//                .filter(punishment1 -> punishment1.getPunishmentType() == PunishmentType.BAN || punishment1.getPunishmentType() == PunishmentType.BLACKLIST)
//                .filter(punishment1 -> punishment1.isActive())
//                .findFirst().orElse(null);
//        if (punishment == null) return;
//
//        StringBuilder kickMessage = new StringBuilder();
//        for (String s : Holiday.getInstance().getPunishmentManager().getKickMessage(punishment)) {
//            kickMessage.append(CC.translate(s)).append("\n");
//        }
//        event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
//        event.setKickMessage(kickMessage.toString());
//    }
}
