package io.github.damt.menu.pagination;

import io.github.damt.menu.Menu;
import io.github.damt.menu.buttons.Button;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

@Getter
@Setter
public abstract class PaginatedMenu extends Menu {

    private NavigationPosition navigationPosition = NavigationPosition.BOTTOM;

    private Button previousPageButton = new Button(Material.ARROW)
            .setDisplayName(ChatColor.GREEN + "Previous Page").setLore(new String[]{
                    CC.translate("&7Click to go back to the previous page")
            });

    private Button nextPageButton = new Button(Material.ARROW)
            .setDisplayName(ChatColor.GREEN + "Next Page").setLore(new String[]{
                    CC.translate("&7Click to jump to the next page")
            });

    private int page = 1;
    private int previousPage = 1;
    private PaginationAction paginationAction = PaginationAction.NEXT;
    private int maxPages;

    /**
     * Constructor to make a new menu object
     *
     * @param player the player to create the menu for
     * @param title  the title to display at the top of the inventory
     * @param size   the size of the inventory
     */
    public PaginatedMenu(Player player, String title, int size) {
        this(player, title, size, 16);
    }

    /**
     * Constructor to make a new menu object
     *
     * @param player   the player to create the menu for
     * @param title    the title to display at the top of the inventory
     * @param size     the size of the inventory
     * @param maxPages the maximum amount of pages
     */
    public PaginatedMenu(Player player, String title, int size, int maxPages) {
        super(player, title, size);
        this.maxPages = maxPages;
    }

    /**
     * Navigate to the next menu page
     */
    public void navigateNext() {
        this.paginationAction = PaginationAction.NEXT;
        this.previousPage = page;

        this.page += 1;
        this.updateMenu();
    }

    /**
     * Navigate to the previous menu page
     */
    public void navigatePrevious() {
        this.paginationAction = PaginationAction.PREVIOUS;
        this.previousPage = page;

        this.page = Math.max(1, this.page - 1);
        this.updateMenu();
    }

    /**
     * Update the menu for the player
     */
    @Override
    public void updateMenu() {
        this.updateMenu(this.getButtonsInRange());
        this.updateMenu(this.getButtonsInRange());
    }

    /**
     * Handle clicking on a button
     *
     * @param event the event called
     */
    @Override
    public void click(InventoryClickEvent event) {
        final Map<Integer, Button> buttons = this.getButtonsInRange();
        final Button button = buttons.get(event.getSlot());

        if (button == null) {
            event.setCancelled(true);
            return;
        }

        if (button.getClickAction() != null) {
            button.getClickAction().accept(event);
        }
    }

    /**
     * Get the list of buttons in the
     * range of the current page.
     *
     * @return the list of buttons
     */
    public Map<Integer, Button> getButtonsInRange() {
        return this.navigationPosition.getButtonsInRange(this.getButtons(), this);
    }

    /**
     * Get the list of buttons for the navigation bar.
     * <p>
     * These buttons will be displayed independent
     * of the current page of the menu.
     *
     * @return the list of buttons
     */
    public Map<Integer, Button> getNavigationBar() {
        return this.navigationPosition.getNavigationButtons(this);
    }
}