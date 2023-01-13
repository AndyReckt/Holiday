package me.andyreckt.holiday.staff.server;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.other.Tasks;
import me.andyreckt.holiday.staff.Staff;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class ModListeners implements Listener {

        @EventHandler
        public void onJoinSetStaffMode(PlayerJoinEvent event) {
            Tasks.runLater(() -> {
                if(Bukkit.getPlayer(event.getPlayer().getUniqueId()) != null) {
                    Profile profile = Holiday.getInstance().getApi().getProfile(event.getPlayer().getUniqueId());
                    if(profile.getStaffSettings().isStaffMode()) Staff.getInstance().getStaffManager().toggleStaffMode(event.getPlayer(), true, false);
                }
            }, 10L);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onLeaveStaffDestroy(PlayerQuitEvent event) {
            Profile profile = Holiday.getInstance().getApi().getProfile(event.getPlayer().getUniqueId());
            if(profile.getStaffSettings().isStaffMode()) Staff.getInstance().getStaffManager().toggleStaffMode(event.getPlayer(), false, false);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onKickStaffDestroy(PlayerKickEvent event) {
            Profile profile = Holiday.getInstance().getApi().getProfile(event.getPlayer().getUniqueId());
            if(profile.getStaffSettings().isStaffMode()) Staff.getInstance().getStaffManager().toggleStaffMode(event.getPlayer(), false, false);
        }

        @EventHandler
        public void onFood(FoodLevelChangeEvent event) {
            Profile profile = Holiday.getInstance().getApi().getProfile(event.getEntity().getUniqueId());
            if(!profile.getStaffSettings().isStaffMode()) return;
            event.setCancelled(true);
            ((Player) event.getEntity()).setSaturation(20);
            ((Player) event.getEntity()).setFoodLevel(20);
        }

        @EventHandler
        public void onItemDrop(PlayerDropItemEvent event) {
            Profile profile = Holiday.getInstance().getApi().getProfile(event.getPlayer().getUniqueId());
            if(!profile.getStaffSettings().isStaffMode()) return;
            if (event.getPlayer().hasMetadata("staff.build")) return;
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onBlockPlace(BlockPlaceEvent event) {
            Profile profile = Holiday.getInstance().getApi().getProfile(event.getPlayer().getUniqueId());
            if(!profile.getStaffSettings().isStaffMode()) return;
            if (event.getPlayer().hasMetadata("staff.build")) return;
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onBlockBreak(BlockBreakEvent event) {
            Profile profile = Holiday.getInstance().getApi().getProfile(event.getPlayer().getUniqueId());
            if(!profile.getStaffSettings().isStaffMode()) return;
            if (event.getPlayer().hasMetadata("staff.build")) return;
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onItemPickup(PlayerPickupItemEvent event) {
            Profile profile = Holiday.getInstance().getApi().getProfile(event.getPlayer().getUniqueId());
            if(!profile.getStaffSettings().isStaffMode()) return;
            if (event.getPlayer().hasMetadata("staff.build")) return;
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            Profile profile = Holiday.getInstance().getApi().getProfile(event.getWhoClicked().getUniqueId());
            if(!profile.getStaffSettings().isStaffMode()) return;
            if (event.getWhoClicked().hasMetadata("staff.build")) return;
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
        }

        @EventHandler
        public void onDamageTake(EntityDamageEvent event) {
            if (!(event.getEntity() instanceof Player)) return;
            Player player = (Player) event.getEntity();
            Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
            if(!profile.getStaffSettings().isStaffMode()) return;
            event.setCancelled(true);
        }
        @EventHandler
        public void onDamageDealt(EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Player)) return;
            Player player = (Player) event.getDamager();
            Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
            if(!profile.getStaffSettings().isStaffMode()) return;
            if (!profile.getStaffSettings().isVanished()) return;
            event.setCancelled(true);
        }

        @EventHandler
        public void onLogoutRemoveBuild(PlayerQuitEvent event) {
            if (event.getPlayer().hasMetadata("staff.build")) event.getPlayer().removeMetadata("staff.build", Staff.getInstance());
        }
    }