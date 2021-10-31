package me.andyreckt.holiday.utils.inventory.crafter;

import org.bukkit.event.inventory.*;
import org.bukkit.entity.*;
import java.util.*;

public abstract class IQuickInventory
{
     String name;
     int size;
     InventoryType inventoryType;
     boolean closable;
    
    public IQuickInventory(final String name, final int size) {
        this.name = name;
        this.size = size;
        this.closable = true;
    }
    
    public IQuickInventory(final String name, final InventoryType inventoryType) {
        this.name = name;
        this.inventoryType = inventoryType;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public InventoryType getInventoryType() {
        return this.inventoryType;
    }
    
    public void setInventoryType(final InventoryType inventoryType) {
        this.inventoryType = inventoryType;
    }
    
    public boolean isClosable() {
        return this.closable;
    }
    
    public void setClosable(final boolean closable) {
        this.closable = closable;
    }
    
    public void open(final Player player) {
        System.out.println("[GUIS] Opened " + this.getClass().getSimpleName() + " gui for " + player.getName());
        QuickInventory.open(player, this);
    }
    
    public List<QuickInventory> getPlayersOpenInventory() {
        return QuickInventoryManager.getPlayerOpenInventory(this);
    }
    
    public abstract void contents(final QuickInventory p0);
}
