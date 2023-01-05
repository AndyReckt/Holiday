package me.andyreckt.holiday.staff.server;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.staff.util.item.Items;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ModItems implements Listener {
        @EventHandler
        public void onClick(PlayerInteractEvent event) {
            if (event.getItem() == null) return;
            if (event.getItem().getItemMeta() == null) return;
            Profile profile = Holiday.getInstance().getApi().getProfile(event.getPlayer().getUniqueId());
            if (!profile.getStaffSettings().isStaffMode()) return;
            for (Items items : Items.values()) {
                 if (items.getEvent() == null) continue;
                 if (!(items.getEvent() instanceof PlayerInteractEvent)) continue;
                 if (items.getItem().isSimilar(event.getItem())) {
                     items.accept(event);
                 }
            }
        }

        @EventHandler
        public void onEntityClick(PlayerInteractEntityEvent event) {
            if (event.getPlayer().getItemInHand() == null) return;
            if (event.getPlayer().getItemInHand().getItemMeta() == null) return;
            Profile profile = Holiday.getInstance().getApi().getProfile(event.getPlayer().getUniqueId());
            if (!profile.getStaffSettings().isStaffMode()) return;
            for (Items items : Items.values()) {
                if (items.getEvent() == null) continue;
                if (!(items.getEvent() instanceof PlayerInteractEntityEvent)) continue;
                if (items.getItem().isSimilar(event.getPlayer().getItemInHand())) {
                    items.accept(event);
                }
            }
        }
    }