package me.andyreckt.holiday.utils.inventory.crafter;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.utils.inventory.item.QuickItem;
import org.bukkit.inventory.*;
import java.util.function.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.plugin.*;
import org.bukkit.event.inventory.*;
import org.bukkit.entity.*;

public class QuickInventory
{
     Inventory inventory;
     QuickData quickData;
     final IQuickInventory iQuickInventory;
    
     QuickInventory(final int size, final InventoryType inventoryType, final String name, final boolean closable, final IQuickInventory iQuickInventory) {
        if (inventoryType == null) {
            this.inventory = Bukkit.createInventory((InventoryHolder)null, size, name);
        }
        else {
            this.inventory = Bukkit.createInventory((InventoryHolder)null, inventoryType, name);
        }
        this.quickData = new QuickData(closable, iQuickInventory);
        this.iQuickInventory = iQuickInventory;
    }
    
    public void addItem(final ItemStack itemStack) {
        this.addItem(itemStack, null, true);
    }
    
    public void addItem(final ItemStack itemStack, final boolean cancelled) {
        this.addItem(itemStack, null, cancelled);
    }
    
    public void addItem(final ItemStack itemStack, final Consumer<QuickEvent> consumer) {
        this.addItem(itemStack, consumer, true);
    }
    
    public void addItem(final ItemStack itemStack, final Consumer<QuickEvent> consumer, final boolean cancelled) {
        final int slot = this.inventory.firstEmpty();
        this.inventory.addItem(itemStack);
        if (consumer != null) {
            this.quickData.getSlovenlier().put(slot, consumer);
        }
        this.quickData.getSlotCancelled().put(slot, cancelled);
    }
    
    public void setItem(final ItemStack itemStack, final int slot) {
        this.setItem(Collections.singletonList(slot), itemStack, null, true);
    }
    
    public void setItem(final ItemStack itemStack, final boolean cancelled, final int slot) {
        this.setItem(Collections.singletonList(slot), itemStack, null, cancelled);
    }
    
    public void setItem(final ItemStack itemStack, final Consumer<QuickEvent> consumer, final int slot) {
        this.setItem(Collections.singletonList(slot), itemStack, consumer, true);
    }
    
    public void setItem(final ItemStack itemStack, final Consumer<QuickEvent> consumer, final boolean cancelled, final int slot) {
        this.setItem(Collections.singletonList(slot), itemStack, consumer, cancelled);
    }
    
    public void setItem(final List<Integer> slot, final ItemStack itemStack) {
        this.setItem(slot, itemStack, null, true);
    }
    
    public void setItem(final List<Integer> slot, final ItemStack itemStack, final boolean cancelled) {
        this.setItem(slot, itemStack, null, cancelled);
    }
    
    public void setItem(final List<Integer> slot, final ItemStack itemStack, final Consumer<QuickEvent> consumer) {
        this.setItem(slot, itemStack, consumer, true);
    }
    
    public void setItem(final List<Integer> slot, final ItemStack itemStack, final Consumer<QuickEvent> consumer, final boolean cancelled) {
        for (final int i : slot) {
            this.inventory.setItem(i, itemStack);
            if (consumer != null) {
                this.quickData.getSlovenlier().put(i, consumer);
            }
            this.quickData.getSlotCancelled().put(i, cancelled);
        }
    }
    
    public void setHorizontalLine(final ItemStack itemStack, final int slotfrom, final int slotto) {
        this.setHorizontalLine(itemStack, slotfrom, slotto, null, true);
    }
    
    public void setHorizontalLine(final ItemStack itemStack, final int slotfrom, final int slotto, final boolean cancelled) {
        this.setHorizontalLine(itemStack, slotfrom, slotto, null, cancelled);
    }
    
    public void setHorizontalLine(final ItemStack itemStack, final int slotfrom, final int slotto, final Consumer<QuickEvent> consumer) {
        this.setHorizontalLine(itemStack, slotfrom, slotto, consumer, true);
    }
    
    public void setHorizontalLine(final ItemStack itemStack, final int slotfrom, final int slotto, final Consumer<QuickEvent> consumer, final boolean cancelled) {
        for (int i = slotfrom; i <= slotto; ++i) {
            this.inventory.setItem(i, itemStack);
            if (consumer != null) {
                this.quickData.getSlovenlier().put(i, consumer);
            }
            this.quickData.getSlotCancelled().put(i, cancelled);
        }
    }
    
    public void setVerticalLine(final ItemStack itemStack, final int slotfrom, final int slotto) {
        this.setVerticalLine(itemStack, slotfrom, slotto, null, true);
    }
    
    public void setVerticalLine(final ItemStack itemStack, final int slotfrom, final int slotto, final boolean cancelled) {
        this.setVerticalLine(itemStack, slotfrom, slotto, null, cancelled);
    }
    
    public void setVerticalLine(final ItemStack itemStack, final int slotfrom, final int slotto, final Consumer<QuickEvent> consumer) {
        this.setVerticalLine(itemStack, slotfrom, slotto, consumer, true);
    }
    
    public void setVerticalLine(final ItemStack itemStack, final int slotfrom, final int slotto, final Consumer<QuickEvent> consumer, final boolean cancelled) {
        for (int i = slotfrom; i <= slotto; i += 9) {
            this.inventory.setItem(i, itemStack);
            this.quickData.getSlotCancelled().put(i, cancelled);
            this.quickData.getSlovenlier().put(i, consumer);
        }
    }
    
    public void setQuickEvent(final int slot, final Consumer<QuickEvent> consumer) {
        this.setQuickEvent(slot, consumer, true);
    }
    
    public void setQuickEvent(final int slot, final Consumer<QuickEvent> consumer, final boolean cancelled) {
        this.quickData.getSlovenlier().remove(slot);
        this.quickData.getSlotCancelled().put(slot, cancelled);
        if (consumer != null) {
            this.quickData.getSlovenlier().put(slot, consumer);
        }
    }
    
    public void updateItem(final String key, final Consumer<TaskUpdate> consumer) {
        this.updateItem(key, consumer, 10);
    }
    
    public void updateItem(final String key, final Consumer<TaskUpdate> consumer, final int period) {
        final TaskUpdate taskUpdate = new TaskUpdate(consumer);
        this.quickData.getUpdateItem().put(key, taskUpdate);
        taskUpdate.runTaskTimer(Holiday.getInstance(), 0L, (long)period);
    }
    
    public TaskUpdate getUpdateItem(final String key) {
        return this.quickData.getUpdateItem().get(key);
    }
    
    public void setClosable(final boolean closable) {
        this.quickData.setClosable(closable);
    }
    
    public void setEventClose(final Consumer<InventoryCloseEvent> eventClose) {
        this.quickData.setCloseEvent(eventClose);
    }
    
    public Player getOwner() {
        return this.quickData.getOwner();
    }
    
    void open(final Player player) {
        player.openInventory(this.inventory);
    }
    
    static void open(final Player player, final IQuickInventory i) {
        if (player.getOpenInventory() != null && QuickInventoryManager.getQuickInventories().containsKey(player)) {
            final QuickInventory inv = QuickInventoryManager.getQuickInventory(player);
            inv.getQuickData().setClosable(true);
            player.closeInventory();
        }
        if (i.getSize() != 9 && i.getSize() != 18 && i.getSize() != 27 && i.getSize() != 36 && i.getSize() != 45 && i.getSize() != 54) {
            throw new ArrayIndexOutOfBoundsException("Error in " + i.getClass().getSimpleName() + " cause : size of inventory is " + i.getSize());
        }
        final QuickInventory inventory = new QuickInventory(i.getSize(), i.getInventoryType(), i.getName(), i.isClosable(), i);
        inventory.quickData.setOwner(player);
        i.contents(inventory);
        QuickInventoryManager.getQuickInventories().put(player, inventory);
        inventory.open(player);
    }
    
    public void close(final Player player) {
        this.quickData.setClosable(true);
        player.closeInventory();
    }
    
    public void update(final Player player, final IQuickInventory i) {
        this.getInventory().clear();
        i.contents(this);
    }
    
    public QuickData getQuickData() {
        return this.quickData;
    }
    
    public Inventory getInventory() {
        return this.inventory;
    }
    
    public IQuickInventory getiQuickInventory() {
        return this.iQuickInventory;
    }
}
