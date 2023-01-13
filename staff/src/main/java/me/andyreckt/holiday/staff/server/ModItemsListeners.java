package me.andyreckt.holiday.staff.server;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.other.Cooldown;
import me.andyreckt.holiday.staff.util.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModItemsListeners implements Listener {

        private final Map<UUID, Cooldown> cooldownMap = new HashMap<>();

        @EventHandler
        public void onClick(PlayerInteractEvent event) {
            if (event.getItem() == null) return;
            Profile profile = Holiday.getInstance().getApi().getProfile(event.getPlayer().getUniqueId());
            if (!profile.getStaffSettings().isStaffMode()) return;
            for (Items items : Items.values()) {
                 if (items.getEvent() == null) continue;
                 if (!(items.getEvent() instanceof PlayerInteractEvent)) continue;
                 if (event.getPlayer().getItemInHand().isSimilar(items.getItem())) {
                     if (cooldown(event.getPlayer().getUniqueId())) return;
                     items.accept(event);
                 }
            }
        }

        @EventHandler
        public void onEntityClick(PlayerInteractEntityEvent event) {
            if (event.getPlayer().getItemInHand() == null) return;
            Profile profile = Holiday.getInstance().getApi().getProfile(event.getPlayer().getUniqueId());
            if (!profile.getStaffSettings().isStaffMode()) return;
            for (Items items : Items.values()) {
                if (items.getEvent() == null) continue;
                if (!(items.getEvent() instanceof PlayerInteractEntityEvent)) continue;
                if (event.getPlayer().getItemInHand().isSimilar(items.getItem())) {
                    if (cooldown(event.getPlayer().getUniqueId())) return;
                    items.accept(event);
                }
            }
        }

        @EventHandler
        public void onPlace(BlockPlaceEvent event) {
            if (event.getItemInHand() == null) return;
            Profile profile = Holiday.getInstance().getApi().getProfile(event.getPlayer().getUniqueId());
            if (!profile.getStaffSettings().isStaffMode()) return;
            for (Items items : Items.values()) {
                if (event.getItemInHand().isSimilar(items.getItem())) event.setCancelled(true);
            }
        }

        @EventHandler
        public void onDrop(PlayerDropItemEvent event) {
            Profile profile = Holiday.getInstance().getApi().getProfile(event.getPlayer().getUniqueId());
            if (!profile.getStaffSettings().isStaffMode()) return;
            for (Items items : Items.values()) {
                if (event.getItemDrop().getItemStack().isSimilar(items.getItem())) event.setCancelled(true);
            }
        }


        private boolean cooldown(UUID uuid) {
            if (cooldownMap.containsKey(uuid)) {
                Cooldown cooldown = cooldownMap.get(uuid);
                if (cooldown.hasExpired()) {
                    cooldownMap.remove(uuid);
                    return false;
                } else {
                    return true;
                }
            }
            cooldownMap.put(uuid, Cooldown.fromSeconds(1));
            return false;
        }
    }