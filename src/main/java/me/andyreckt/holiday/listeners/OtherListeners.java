package me.andyreckt.holiday.listeners;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.ClickablePacket;
import me.andyreckt.holiday.other.enums.BroadcastType;
import me.andyreckt.holiday.other.menu.InvseeMenu;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.server.Server;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.PlayerUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
    @EventHandler
    public void invseeClose(InventoryCloseEvent event) {
        InvseeMenu.invMap.remove((Player) event.getPlayer());
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        if (!event.getPlayer().hasMetadata("frozen")) return;
        Holiday.getInstance().getRedis().sendPacket(new ClickablePacket(
                "&c" + event.getPlayer().getName() + " has logged out whilst frozen",
                "&7&oClick here to ban",
                ClickEvent.Action.RUN_COMMAND,
                "/ban " + event.getPlayer().getName() + " Logged out whilst frozen -s",
                BroadcastType.STAFF
        ));
    }

    @EventHandler
    public void onLoginFrozen(PlayerJoinEvent event) {
        if (event.getPlayer().hasMetadata("frozen")) {
            event.getPlayer().removeMetadata("frozen", Holiday.getInstance());
            PlayerUtil.freeze(event.getPlayer(), false);
        }
    }
}
