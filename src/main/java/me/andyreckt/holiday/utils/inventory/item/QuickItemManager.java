package me.andyreckt.holiday.utils.inventory.item;

import java.util.function.*;
import com.google.gson.*;
import org.bukkit.plugin.java.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.bukkit.inventory.*;

public class QuickItemManager
{
     static Map<String, Consumer<QuickEvent>> quickItem;
     static Gson gson;
    
    public QuickItemManager(final JavaPlugin javaPlugin) {
        QuickItemManager.gson = new Gson();
        QuickItemManager.quickItem = new HashMap<>();
        Bukkit.getServer().getPluginManager().registerEvents(new QuickListeners(this), javaPlugin);
    }
    
    static void registerItem(final QuickItem quickItem, final ItemStack itemStack, final Consumer<QuickEvent> consumer) {
        System.out.println("[ITEMS] registered item " + quickItem.getClass().getSimpleName());
        QuickItemManager.quickItem.put(QuickItemManager.gson.toJson(new QuickItemData(itemStack.getType(), itemStack.getData().getData(), itemStack.getItemMeta())), consumer);
    }
    
    Consumer<QuickEvent> getEventQuickItem(final ItemStack itemStack) {
        return QuickItemManager.quickItem.get(QuickItemManager.gson.toJson(new QuickItemData(itemStack.getType(), itemStack.getData().getData(), itemStack.getItemMeta())));
    }

}
