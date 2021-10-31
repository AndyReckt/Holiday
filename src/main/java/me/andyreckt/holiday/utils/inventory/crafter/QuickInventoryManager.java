package me.andyreckt.holiday.utils.inventory.crafter;

import org.bukkit.entity.*;
import org.bukkit.plugin.java.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import java.util.*;

public class QuickInventoryManager
{
     static Map<Player, QuickInventory> quickInventories;
    
    public QuickInventoryManager(final JavaPlugin javaPlugin) {
        QuickInventoryManager.quickInventories = new HashMap<Player, QuickInventory>();
        Bukkit.getPluginManager().registerEvents((Listener)new QuickListener(javaPlugin, this), (Plugin)javaPlugin);
    }
    
    static QuickInventory getQuickInventory(final Player player) {
        return QuickInventoryManager.quickInventories.get(player);
    }
    
    static Map<Player, QuickInventory> getQuickInventories() {
        return QuickInventoryManager.quickInventories;
    }
    
    static List<QuickInventory> getPlayerOpenInventory(final IQuickInventory iQuickInventory) {
        final List<QuickInventory> inventories = new ArrayList<>();
        QuickInventoryManager.quickInventories.forEach((player, quickInventory) -> {
            if (quickInventory.getQuickData().getiQuickInventory().getClass() == iQuickInventory.getClass()) {
                inventories.add(quickInventory);
            }
        });
        return inventories;
    }
}
