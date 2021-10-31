package me.andyreckt.holiday.utils.inventory.crafter;

import java.util.HashMap;
import java.util.function.*;

import me.andyreckt.holiday.Holiday;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import org.bukkit.event.*;
import net.minecraft.server.v1_8_R3.*;

@SuppressWarnings("all") public class AnvilGUI
{
     final Consumer<AnvilClickEvent> onClick;
     Player player;
     HashMap<AnvilSlot, ItemStack> items;
     Inventory inv;
     static Listener listener;
    
    public AnvilGUI(final Consumer<AnvilClickEvent> onClick) {
        this.items = new HashMap<AnvilSlot, ItemStack>();
        this.onClick = onClick;
        AnvilGUI.listener = new Listener() {
            @EventHandler
            public void onInventoryClick(final InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player && event.getInventory().equals(AnvilGUI.this.inv)) {
                    event.setCancelled(true);
                    final ItemStack item = event.getCurrentItem();
                    final int slot = event.getRawSlot();
                    onClick.accept(new AnvilClickEvent((Player)event.getWhoClicked(), AnvilSlot.bySlot(slot), item));
                }
            }
            
            @EventHandler
            public void onInventoryClose(final InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player) {
                    final Inventory inv = event.getInventory();
                    if (inv.equals(AnvilGUI.this.inv)) {
                        inv.clear();
                        AnvilGUI.destroy((Player)event.getPlayer());
                    }
                }
            }
            
            @EventHandler
            public void onPlayerQuit(final PlayerQuitEvent event) {
                if (event.getPlayer().equals(AnvilGUI.this.player)) {
                    AnvilGUI.destroy(event.getPlayer());
                }
            }
        };
        Bukkit.getPluginManager().registerEvents(AnvilGUI.listener, Holiday.getInstance());
    }
    
    public void setSlot(final AnvilSlot slot, final ItemStack item) {
        this.items.put(slot, item);
    }
    
    public void open(final Player player) {
        this.player = player;
        final EntityPlayer p = ((CraftPlayer)player).getHandle();
        final AnvilContainer container = new AnvilContainer((EntityHuman)p);
        this.inv = container.getBukkitView().getTopInventory();
        for (final AnvilSlot slot : this.items.keySet()) {
            this.inv.setItem(slot.getSlot(), (ItemStack)this.items.get(slot));
        }
        final int c = p.nextContainerCounter();
        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", (IChatBaseComponent)new ChatMessage("Repairing", new Object[0]), 0));
        p.activeContainer = container;
        p.activeContainer.windowId = c;
        p.activeContainer.addSlotListener(p);
    }
    
    public static void destroy(final Player player) {
        HandlerList.unregisterAll(AnvilGUI.listener);
        if (player.getOpenInventory() != null) {
            player.getOpenInventory().getTopInventory().clear();
            player.closeInventory();
        }
    }
    
     class AnvilContainer extends ContainerAnvil
    {
        public AnvilContainer(final EntityHuman entity) {
            super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
        }
        
        public boolean a(final EntityHuman entityhuman) {
            return true;
        }
    }
    
    public enum AnvilSlot
    {
        INPUT_LEFT(0), 
        INPUT_RIGHT(1), 
        OUTPUT(2);
        
         int slot;
        
         AnvilSlot(final int slot) {
            this.slot = slot;
        }
        
        public int getSlot() {
            return this.slot;
        }
        
        public static AnvilSlot bySlot(final int slot) {
            for (final AnvilSlot anvilSlot : values()) {
                if (anvilSlot.getSlot() == slot) {
                    return anvilSlot;
                }
            }
            return null;
        }
    }
    
    public class AnvilClickEvent
    {
         final Player player;
         final AnvilSlot slot;
         final ItemStack item;
        
        public AnvilClickEvent(final Player player, final AnvilSlot slot, final ItemStack item) {
            this.player = player;
            this.slot = slot;
            this.item = item;
        }
        
        public Player getPlayer() {
            return this.player;
        }
        
        public AnvilSlot getSlot() {
            return this.slot;
        }
        
        public ItemStack getItem() {
            return this.item;
        }
    }
}
