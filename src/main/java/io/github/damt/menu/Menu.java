package io.github.damt.menu;

import io.github.damt.menu.buttons.Button;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Getter
@Setter
public abstract class Menu {


    private final Player player;
    public final String title;
    private final int size;

    private boolean updated;

    // the inventory to use if the inventory already exists,
    // to avoid re-opening the inventory whenever updating.
    private Inventory inventory;

    private MenuType menuType = MenuType.INVENTORY;
    private MenuUpdateType updateType = MenuUpdateType.NONE;

    // the button type used for filling the inventory slots
    private ItemStack fillerType = new ItemStack(Material.STAINED_GLASS_PANE);
    private Button fillerTypeButton = new Button(fillerType).setData(DyeColor.BLACK.getDyeData()).setDisplayName(" ");


    public Menu(Player player, String title, int size) {
        this.player = player;
        this.size = size;
        this.title = title;

        this.registerMenu();
    }

    /**
     * Updates the buttons
     */

    public void updateMenu() {
        this.updateMenu(getButtons());
    }

    /**
     * Updates the buttons specified
     * If you'd like to change to older versions
     * Change Inventory#setStorageContents to Ine
     *
     * @param buttonMap the integer/button map
     */

    public void updateMenu(Map<Integer, Button> buttonMap) {
        final Inventory inventory = this.menuType.createInventory(this);

        buttonMap.keySet().forEach(integer -> {
            inventory.setItem(integer, buttonMap.get(integer).toItemStack());
        });

        if (player.getOpenInventory().getTitle().equalsIgnoreCase(getTitle()) && player.getOpenInventory().getTopInventory().getSize() == getSize()) {
            player.getOpenInventory().getTopInventory().setContents(inventory.getContents());
            player.updateInventory();
            return;
        }

        player.openInventory(inventory);
        this.registerMenu();
    }

    /**
     * The method to get the buttons for the current inventory tick
     * 
     * a button to a slot.
     */
    public abstract Map<Integer, Button> getButtons();

    /**
     * Register the menu to the menu handler
     */
    public void registerMenu() {
        MenuHandler.getInstance().register(this.player, this);
    }

    /**
     * Redirect the player's menu to a new menu
     *
     * @param menu the menu to redirect it to
     */
    public void redirect(Menu menu) {
        menu.updateMenu();
        this.registerMenu();
    }

    /**
     * Handle clicking on a button
     *
     * @param event the event called
     */
    public void click(InventoryClickEvent event) {
        final Button button = getButtons().get(event.getSlot());

        if (button == null) {
            event.setCancelled(true);
            return;
        }

        if (button.getClickAction() != null) {
            button.getClickAction().accept(event);
        }
    }

    /**
     * Handle the player closing the inventory
     *
     * @param event the event called
     */
    public void handleClose(InventoryCloseEvent event) {
        this.updated = false;
        MenuHandler.getInstance().unregister((Player) event.getPlayer());
    }

    /**
     * Handles the closing of the inventory
     *
     * @param player player closing the menu
     * @param event even called
     */

    public void close(Player player, InventoryCloseEvent event) {
    }

}
