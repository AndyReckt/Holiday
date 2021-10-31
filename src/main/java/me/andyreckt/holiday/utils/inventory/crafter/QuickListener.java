package me.andyreckt.holiday.utils.inventory.crafter;

import org.bukkit.plugin.java.*;
import org.bukkit.entity.*;
import java.util.function.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.event.server.*;

class QuickListener implements Listener
{
     JavaPlugin javaPlugin;
     QuickInventoryManager quickInventoryManager;
    
    public QuickListener(final JavaPlugin javaPlugin, final QuickInventoryManager quickInventoryManager) {
        this.javaPlugin = javaPlugin;
        this.quickInventoryManager = quickInventoryManager;
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        final int slot = event.getSlot();
        final QuickInventory inv = QuickInventoryManager.getQuickInventory(player);
        if (inv != null) {
            if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof Player) {
                event.setCancelled(true);
                return;
            }
            final QuickData quickEventData = inv.getQuickData();
            final QuickEvent quickEvent = new QuickEvent(event);
            System.out.println("[GUI] " + player.getName() + " clicked on slot " + slot + " in " + inv.getiQuickInventory().getClass().getSimpleName());
            if (quickEventData.getSlotCancelled().containsKey(slot) && quickEventData.getSlotCancelled().get(slot)) {
                event.setCancelled(true);
            }
            if (quickEventData.getSlovenlier().containsKey(slot) && quickEventData.getSlovenlier() != null && quickEventData.getSlovenlier().get(slot) != null) {
                quickEventData.getSlovenlier().get(slot).accept(quickEvent);
            }
        }
    }
    
    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        final Player player = (Player)event.getPlayer();
        final QuickInventoryManager quickInventoryManager = this.quickInventoryManager;
        final QuickInventory inv = QuickInventoryManager.getQuickInventory(player);
        if (inv != null) {
            if (inv.getQuickData().getCloseEvent() != null) {
                inv.getQuickData().getCloseEvent().accept(event);
            }
            if (!inv.getQuickData().isClosable()) {
                Bukkit.getScheduler().runTaskLater((Plugin)this.javaPlugin, () -> inv.open(player), 2L);
            }
            else {
                inv.getQuickData().getUpdateItem().forEach((s, taskUpdate) -> taskUpdate.cancel());
                final QuickInventoryManager quickInventoryManager2 = this.quickInventoryManager;
                QuickInventoryManager.getQuickInventories().remove(player);
            }
        }
    }
    
    @EventHandler
    public void onDisable(final PluginDisableEvent event) {
        Bukkit.getOnlinePlayers().forEach(o -> {
            QuickInventoryManager quickInventoryManager = this.quickInventoryManager;
            if (QuickInventoryManager.getQuickInventories().containsKey(o)) {
                o.closeInventory();
            }
        });
    }
}
