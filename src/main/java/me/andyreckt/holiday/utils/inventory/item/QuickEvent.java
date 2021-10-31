package me.andyreckt.holiday.utils.inventory.item;

import org.bukkit.event.player.*;
import org.bukkit.entity.*;
import org.bukkit.event.block.*;

public class QuickEvent
{
     PlayerInteractEvent event;
    
    public QuickEvent(final PlayerInteractEvent event) {
        this.event = event;

    }
    
    public Player getPlayer() {
        return this.event.getPlayer();
    }
    
    public Action getAction() {
        return this.event.getAction();
    }
}
