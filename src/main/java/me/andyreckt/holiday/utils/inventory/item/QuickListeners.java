package me.andyreckt.holiday.utils.inventory.item;

import org.bukkit.event.player.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import java.util.function.*;
import org.bukkit.event.*;

class QuickListeners implements Listener
{
     QuickItemManager quickItemManager;
    
    QuickListeners(final QuickItemManager quickItemManager) {
        this.quickItemManager = quickItemManager;
    }
    
    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();
        if (itemStack == null) {
            return;
        }
        final Consumer<QuickEvent> consumer = this.quickItemManager.getEventQuickItem(itemStack);
        if (consumer != null) {
            System.out.println("[ITEMS] " + player.getName() + " clicked on " + itemStack);
            consumer.accept(new QuickEvent(event));
        }
    }
}
