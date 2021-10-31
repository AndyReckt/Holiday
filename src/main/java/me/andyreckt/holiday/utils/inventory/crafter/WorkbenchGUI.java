package me.andyreckt.holiday.utils.inventory.crafter;

import java.util.function.*;

import me.andyreckt.holiday.Holiday;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import java.util.*;
import org.bukkit.event.*;
import net.minecraft.server.v1_8_R3.*;

public class WorkbenchGUI
{
     final Consumer<WorkbenchClickEvent> onClick;
     Player player;
     HashMap<WorkbenchSlot, ItemStack> items;
     Inventory inv;
     static Listener listener;
    
    public WorkbenchGUI(final Consumer<WorkbenchClickEvent> onClick) {
        this.items = new HashMap<WorkbenchSlot, ItemStack>();
        this.onClick = onClick;
        WorkbenchGUI.listener = (Listener)new Listener() {
            @EventHandler
            public void onInventoryClick(final InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player && event.getInventory().equals(WorkbenchGUI.this.inv)) {
                    event.setCancelled(true);
                    final ItemStack item = event.getCurrentItem();
                    final int slot = event.getRawSlot();
                    onClick.accept(new WorkbenchClickEvent((Player)event.getWhoClicked(), WorkbenchSlot.bySlot(slot), item));
                }
            }
            
            @EventHandler
            public void onInventoryClose(final InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player) {
                    final Inventory inv = event.getInventory();
                    if (inv.equals(WorkbenchGUI.this.inv)) {
                        inv.clear();
                        WorkbenchGUI.destroy((Player)event.getPlayer());
                    }
                }
            }
            
            @EventHandler
            public void onPlayerQuit(final PlayerQuitEvent event) {
                if (event.getPlayer().equals(WorkbenchGUI.this.player)) {
                    WorkbenchGUI.destroy(event.getPlayer());
                }
            }
        };
        Bukkit.getPluginManager().registerEvents(WorkbenchGUI.listener, Holiday.getInstance());
    }
    
    public void setSlot(final WorkbenchSlot slot, final ItemStack item) {
        this.items.put(slot, item);
    }
    
    public void open(final Player player) {
        this.player = player;
        final EntityPlayer p = ((CraftPlayer)player).getHandle();
        final WorkbenchContainer container = new WorkbenchContainer((EntityHuman)p);
        this.inv = container.getBukkitView().getTopInventory();
        for (final WorkbenchSlot slot : this.items.keySet()) {
            this.inv.setItem(slot.getSlot(), (ItemStack)this.items.get(slot));
        }
        final int c = p.nextContainerCounter();
        p.playerConnection.sendPacket((Packet)new PacketPlayOutOpenWindow(c, "minecraft:crafting_table", (IChatBaseComponent)new ChatMessage("WorkBench", new Object[0]), 0));
        p.activeContainer = (Container)container;
        p.activeContainer.windowId = c;
        p.activeContainer.addSlotListener((ICrafting)p);
    }
    
    public static void destroy(final Player player) {
        HandlerList.unregisterAll(WorkbenchGUI.listener);
        player.getOpenInventory().getTopInventory().clear();
        player.closeInventory();
    }
    
     class WorkbenchContainer extends ContainerWorkbench
    {
        public WorkbenchContainer(final EntityHuman entity) {
            super(entity.inventory, entity.world, new BlockPosition(0, 0, 0));
        }
        
        public boolean a(final EntityHuman entityhuman) {
            return true;
        }
    }
    
    public enum WorkbenchSlot
    {
        RESULT(0), 
        HIGH_LEFT(1), 
        HIGH_CENTER(2), 
        HIGH_RIGHT(3), 
        MIDDLE_LEFT(4), 
        MIDDLE_CENTER(5), 
        MIDDLE_RIGHT(6), 
        DOWN_LEFT(7), 
        DOWN_CENTER(8), 
        DOWN_RIGHT(9);
        
         int slot;
        
         WorkbenchSlot(final int slot) {
            this.slot = slot;
        }
        
        public int getSlot() {
            return this.slot;
        }
        
        public static WorkbenchSlot bySlot(final int slot) {
            final WorkbenchSlot[] values = values();
            final int length = values.length;
            final int n = 0;
            if (n >= length) {
                return null;
            }
            final WorkbenchSlot workbenchSlot = values[n];
            if (workbenchSlot.getSlot() == slot) {
                return workbenchSlot;
            }
            return null;
        }
    }
    
    public class WorkbenchClickEvent
    {
         final Player player;
         final WorkbenchSlot slot;
         final ItemStack item;
        
        public WorkbenchClickEvent(final Player player, final WorkbenchSlot slot, final ItemStack item) {
            this.player = player;
            this.slot = slot;
            this.item = item;
        }
        
        public Player getPlayer() {
            return this.player;
        }
        
        public WorkbenchSlot getSlot() {
            return this.slot;
        }
        
        public ItemStack getItem() {
            return this.item;
        }
    }
}
