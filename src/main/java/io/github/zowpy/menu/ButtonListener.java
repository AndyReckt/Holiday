package io.github.zowpy.menu;

import io.github.zowpy.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import javax.swing.text.TabableView;

public class ButtonListener implements Listener {
    private final Plugin plugin;

    public ButtonListener(final Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onButtonPress(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Menu openMenu = Menu.currentlyOpenedMenus.get(player.getUniqueId());
        if (openMenu != null) {
            if (event.getSlot() != event.getRawSlot()) {
                if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                    event.setCancelled(true);
                }
                return;
            }
            if (openMenu.getButtons().containsKey(event.getSlot())) {
                final Button button = openMenu.getButtons().get(event.getSlot());
                final boolean cancel = button.shouldCancel(player, event.getSlot(), event.getClick());
                if (!cancel && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);
                    if (event.getCurrentItem() != null) {
                        player.getInventory().addItem(event.getCurrentItem());
                    }
                } else {
                    event.setCancelled(cancel);
                }
                button.clicked(player, event.getSlot(), event.getClick(), event.getHotbarButton());
                if (Menu.currentlyOpenedMenus.containsKey(player.getUniqueId())) {
                    final Menu newMenu = Menu.currentlyOpenedMenus.get(player.getUniqueId());
                    if (newMenu == openMenu) {
                        final boolean buttonUpdate = button.shouldUpdate(player, event.getSlot(), event.getClick());
                        if (buttonUpdate) {
                            openMenu.setClosedByMenu(true);
                            newMenu.openMenu(player);
                        }
                    }
                } else if (button.shouldUpdate(player, event.getSlot(), event.getClick())) {
                    openMenu.setClosedByMenu(true);
                    openMenu.openMenu(player);
                }
                if (event.isCancelled()) {
                    Bukkit.getScheduler().runTaskLater(this.plugin, player::updateInventory, 1L);
                }
            } else if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Tasks.runLater(() -> {
            if ((event.getPlayer().getOpenInventory() == null) || (event.getPlayer().getOpenInventory().getTopInventory() == null)) onInventoryClose((Player) event.getPlayer());
        }, 5L);
    }


    public static void onInventoryClose(Player player) {
        final Menu openMenu = Menu.currentlyOpenedMenus.get(player.getUniqueId());
        if (openMenu != null) {
            openMenu.setInventory(player.getOpenInventory().getTopInventory());
            openMenu.onClose(player);
            Menu.currentlyOpenedMenus.remove(player.getUniqueId());
            if (openMenu instanceof PaginatedMenu) {
                return;
            }
        }
        player.setMetadata("scanglitch", new FixedMetadataValue(Holiday.getInstance(), true));
    }


    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (player.hasMetadata("scanglitch")) {
            player.removeMetadata("scanglitch", this.plugin);
            for (final ItemStack it : player.getInventory().getContents()) {
                if (it != null) {
                    final ItemMeta meta = it.getItemMeta();
                    if (meta != null && meta.hasDisplayName() && meta.getDisplayName().contains("§b§c§d§e")) {
                        player.getInventory().remove(it);
                    }
                }
            }
        }
    }
}
