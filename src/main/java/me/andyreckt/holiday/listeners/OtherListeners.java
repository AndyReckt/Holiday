package me.andyreckt.holiday.listeners;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.server.Server;
import me.andyreckt.holiday.utils.CC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class OtherListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoinWhitelist(PlayerLoginEvent event) {
        if (!(event.getResult() == PlayerLoginEvent.Result.ALLOWED)) return;

        Server data = Holiday.getInstance().getServerHandler().getThisServer();
        Profile profile = Holiday.getInstance().getProfileHandler().getByUUID(event.getPlayer().getUniqueId());
        if (!data.isWhitelisted()) return;
        if (profile.getHighestRank().getPriority() >= data.getWhitelistRank().getPriority() || data.getWhitelistedPlayers().contains(event.getPlayer().getUniqueId())) {
            event.setResult(PlayerLoginEvent.Result.ALLOWED);
        } else {
            event.setKickMessage(CC.translate(Holiday.getInstance().getSettings().getString("SERVER.WHITELISTMESAGE")
                    .replace("<rank>", data.getWhitelistRank().getDisplayName())));
            event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoinFull(PlayerLoginEvent event) {
        if (!(event.getResult() == PlayerLoginEvent.Result.KICK_FULL)) return;
        Profile profile = Holiday.getInstance().getProfileHandler().getByUUID(event.getPlayer().getUniqueId());
        if (Holiday.getInstance().getSettings().getBoolean("SERVER.DONATORJOINFULL")
        && profile.hasPermission("holiday.joinfull")) event.setResult(PlayerLoginEvent.Result.ALLOWED);
    }


}
