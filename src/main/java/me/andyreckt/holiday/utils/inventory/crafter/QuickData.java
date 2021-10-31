package me.andyreckt.holiday.utils.inventory.crafter;

import java.util.function.*;
import org.bukkit.event.inventory.*;
import org.bukkit.entity.*;
import java.util.*;

class QuickData
{
     Map<Integer, Consumer<QuickEvent>> slovenlier;
     Map<Integer, Boolean> slotCancelled;
     Consumer<InventoryCloseEvent> closeEvent;
     Map<String, TaskUpdate> updateItem;
     boolean closable;
     Player owner;
     IQuickInventory iQuickInventory;
    
    QuickData(final boolean closable, final IQuickInventory iQuickInventory) {
        this.slovenlier = new HashMap<Integer, Consumer<QuickEvent>>();
        this.slotCancelled = new HashMap<Integer, Boolean>();
        this.updateItem = new HashMap<String, TaskUpdate>();
        this.closable = closable;
        this.iQuickInventory = iQuickInventory;
    }
    
    Map<Integer, Consumer<QuickEvent>> getSlovenlier() {
        return this.slovenlier;
    }
    
    Map<Integer, Boolean> getSlotCancelled() {
        return this.slotCancelled;
    }
    
    Consumer<InventoryCloseEvent> getCloseEvent() {
        return this.closeEvent;
    }
    
    Map<String, TaskUpdate> getUpdateItem() {
        return this.updateItem;
    }
    
    void setCloseEvent(final Consumer<InventoryCloseEvent> closeEvent) {
        this.closeEvent = closeEvent;
    }
    
    boolean isClosable() {
        return this.closable;
    }
    
    void setClosable(final boolean closable) {
        this.closable = closable;
    }
    
    Player getOwner() {
        return this.owner;
    }
    
    void setOwner(final Player owner) {
        this.owner = owner;
    }
    
    IQuickInventory getiQuickInventory() {
        return this.iQuickInventory;
    }
}
