package me.andyreckt.holiday.utils.inventory.crafter;

import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;

public class QuickEvent
{
     InventoryClickEvent event;
    
    public QuickEvent(final InventoryClickEvent event) {
        this.event = event;
    }
    
    public Player getPlayer() {
        return (Player)this.event.getWhoClicked();
    }
    
    public ClickType getClickType() {
        return this.event.getClick();
    }
    
    public InventoryAction getInventoryAction() {
        return this.event.getAction();
    }
    
    public InventoryClickEvent getEvent() {
        return this.event;
    }
}
