package me.andyreckt.holiday.bukkit.util.menu;

import me.andyreckt.holiday.bukkit.Holiday;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

public abstract class Menu {
    public static Map<UUID, Menu> currentlyOpenedMenus = new HashMap<>();
    private Map<Integer, Button> buttons = new HashMap<>();
    private boolean autoUpdate = false;
    private boolean updateAfterClick = true;
    private boolean closedByMenu = false;
    private boolean placeholder = false;
    private Button placeholderButton = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " ");
    private Inventory inventory;

    public static Map<UUID, Menu> getCurrentlyOpenedMenus() {
        return currentlyOpenedMenus;
    }

    public Map<Integer, Button> getButtons() {
        return this.buttons;
    }

    public void setButtons(Map<Integer, Button> buttons) {
        this.buttons = buttons;
    }

    public boolean isAutoUpdate() {
        return this.autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public boolean isUpdateAfterClick() {
        return this.updateAfterClick;
    }

    public void setUpdateAfterClick(boolean updateAfterClick) {
        this.updateAfterClick = updateAfterClick;
    }

    public boolean isClosedByMenu() {
        return this.closedByMenu;
    }

    public void setClosedByMenu(boolean closedByMenu) {
        this.closedByMenu = closedByMenu;
    }

    public boolean isPlaceholder() {
        return this.placeholder;
    }

    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }

    public Button getPlaceholderButton() {
        return this.placeholderButton;
    }

    public void setPlaceholderButton(Button placeholderButton) {
        this.placeholderButton = placeholderButton;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    private ItemStack createItemStack(Player player, Button button) {
        ItemStack item = button.getButtonItem(player);
        if (item.getType() != Material.SKULL_ITEM) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName())
                meta.setDisplayName(meta.getDisplayName() + "§b§c§d§e");
            item.setItemMeta(meta);
        }
        return item;
    }

    public void fill(Map<Integer, Button> buttons, final ItemStack itemStack) {
        IntStream.range(0, getSize()).filter(slot -> (buttons.get(slot) == null))

                .forEach(slot -> buttons.put(slot, new Button() {
                    public ItemStack getButtonItem(Player player) {
                        return itemStack;
                    }
                }));
    }

    public void openMenu(Player player) {
        this.buttons = getButtons(player);
        Menu previousMenu = currentlyOpenedMenus.get(player.getUniqueId());
        this.inventory = null;
        int size = (getSize() == -1) ? size(this.buttons) : getSize();
        boolean update = false;
        String title = getTitle(player);
        if (title.length() > 32)
            title = title.substring(0, 32);
        if (player.getOpenInventory() != null)
            if (previousMenu == null) {
                ButtonListener.onInventoryClose(player);
            } else {
                int previousSize = player.getOpenInventory().getTopInventory().getSize();
                if (previousSize == size && player.getOpenInventory().getTopInventory().getTitle().equals(title)) {
                    this.inventory = player.getOpenInventory().getTopInventory();
                    update = true;
                } else {
                    previousMenu.setClosedByMenu(true);
                    ButtonListener.onInventoryClose(player);
//                    player.closeInventory();
                }
            }
        if (this.inventory == null)
            this.inventory = Bukkit.createInventory(player, size, ChatColor.translateAlternateColorCodes('&', title));
        this.inventory.setContents(new ItemStack[this.inventory.getSize()]);
        currentlyOpenedMenus.put(player.getUniqueId(), this);
        for (Map.Entry<Integer, Button> buttonEntry : this.buttons.entrySet())
            try {
                this.inventory.setItem(buttonEntry.getKey(), createItemStack(player, buttonEntry.getValue()));
            } catch (Exception ignored) {
            }
        if (isPlaceholder())
            for (int index = 0; index < size; index++) {
                if (this.buttons.get(index) == null) {
                    this.buttons.put(index, this.placeholderButton);
                    this.inventory.setItem(index, this.placeholderButton.getButtonItem(player));
                }
            }
        if (update) {
            player.updateInventory();
        } else {
            if (this.inventory instanceof CraftingInventory) return;
            player.openInventory(this.inventory);
        }
        onOpen(player);
        setClosedByMenu(false);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Holiday.getInstance(), () -> currentlyOpenedMenus.put(player.getUniqueId(), this), 1L);
    }

    public int size(Map<Integer, Button> buttons) {
        int highest = 0;
        for (int buttonValue : buttons.keySet()) {
            if (buttonValue > highest)
                highest = buttonValue;
        }
        return (int) (Math.ceil((highest + 1) / 9.0D) * 9.0D);
    }

    public int getSlot(int x, int y) {
        return 9 * y + x;
    }

    public int getSize() {
        return -1;
    }

    public void onOpen(Player player) {
    }

    public void onClose(Player player) {
    }

    protected void surroundButtons(boolean full, Map<Integer, Button> buttons, final ItemStack itemStack) {
        IntStream.range(0, getSize()).filter(slot -> (buttons.get(slot) == null)).forEach(slot -> {
            if (slot < 9 || slot > getSize() - 10 || (full && (slot % 9 == 0 || (slot + 1) % 9 == 0)))
                buttons.put(slot, new Button() {
                    public ItemStack getButtonItem(Player player) {
                        return itemStack;
                    }
                });
        });
    }

    public abstract String getTitle(Player paramPlayer);

    public abstract Map<Integer, Button> getButtons(Player paramPlayer);
}
