package me.andyreckt.holiday.staff.server;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.other.Tasks;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import me.andyreckt.holiday.staff.Staff;
import me.andyreckt.holiday.staff.util.files.SLocale;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FreezeListeners implements Listener {
    @EventHandler
    public void onAttack(EntityDamageEvent event) {
        Entity p = event.getEntity();
        if (!(p instanceof Player)) return;
        if (!p.hasMetadata("frozen")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity p = event.getDamager();
        if (!(p instanceof Player)) return;
        if (!p.hasMetadata("frozen")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        if (p == null) return;
        if (!p.hasMetadata("frozen")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player p = event.getPlayer();
        if (p == null) return;
        if (!p.hasMetadata("frozen")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (p == null) return;
        if (!p.hasMetadata("frozen")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (p == null) return;
        if (!p.hasMetadata("frozen")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (p == null) return;
        if (!p.hasMetadata("frozen")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (p == null) return;
        if (!p.hasMetadata("frozen")) return;
        if (event.getFrom().getX() != event.getTo().getX()
                || event.getFrom().getZ() != event.getTo().getZ()
                || event.getFrom().getY() != event.getTo().getY()) {
            event.getPlayer().teleport(event.getFrom());
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (p == null) return;
        if (!p.hasMetadata("frozen")) return;
        p.removeMetadata("frozen", Staff.getInstance());
        Tasks.runAsyncLater(() -> {
            Profile profile = Holiday.getInstance().getApi().getProfile(p.getUniqueId());
            if (profile.isBanned() || profile.isIpBanned() || profile.isBlacklisted()) return;
            PacketHandler.send(new BroadcastPacket(
                    SLocale.ALERTS_FREEZE_LOGOUT.getString().replace("%player%", profile.getName()),
                    Perms.STAFF_VIEW_NOTIFICATIONS.get()));
        }, 15L);
    }


}