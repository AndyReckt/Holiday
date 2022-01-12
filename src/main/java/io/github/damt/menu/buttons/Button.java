package io.github.damt.menu.buttons;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class Button implements Cloneable {

    private Material material;
    private ItemMeta meta;

    private String displayName;
    private String[] lore;
    private boolean glow;

    private Consumer<InventoryClickEvent> clickAction = event -> event.setCancelled(true);

    private int amount;
    private int customMetaData;
    private byte data;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();

    /**
     * @param material the icon of the button
     */
    public Button(Material material) {
        this(new ItemStack(material));
    }

    /**
     * Make a new {@link Button} object from an {@link ItemStack}
     *
     * @param itemStack the item stack to get it from
     */
    public Button(ItemStack itemStack) {
        this.material = itemStack.getType();
        this.meta = itemStack.getItemMeta();

        this.displayName = meta.getDisplayName();
        this.lore = meta.getLore().toArray(new String[0]);
        this.data = itemStack.getData().getData();
        this.amount = itemStack.getAmount();

        this.enchantments = itemStack.getEnchantments();
    }

    public Button setItemStack(ItemStack itemStack) {
        this.material = itemStack.getType();
        this.meta = itemStack.getItemMeta();

        this.data = itemStack.getData().getData();
        this.amount = itemStack.getAmount();

        this.enchantments = itemStack.getEnchantments();
        return this;
    }

    @Override
    public Button clone() {
        return new Button(this.material)
                .setDisplayName(this.getDisplayName())
                .setAmount(this.getAmount())
                .setClickAction(this.getClickAction())
                .setLore(this.getLore())
                .setData(this.getData());
    }

    /**
     * Set the display name of the button in the menu.
     *
     * @param displayName the display name to set it to
     * @return the current button instance
     */
    public Button setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Set the lore of the button in the menu.
     *
     * @param lore the lore to set it to
     * @return the current button instance
     */

    public Button setLore(String[] lore) {
        this.lore = lore;
        return this;
    }

    /**
     * Set the click action of the button in the menu.
     *
     * @param clickAction the click action to set it to
     * @return the current button instance
     */
    public Button setClickAction(Consumer<InventoryClickEvent> clickAction) {
        this.clickAction = clickAction;
        return this;
    }

    /**
     * Set the amount of the item stack of the button in the menu.
     *
     * @param amount the amount to set it to
     * @return the current button instance
     */
    public Button setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Set the item stack data of the button in the menu.
     *
     * @param data the itemstack data to set it to
     * @return the current button instance
     */
    public Button setData(byte data) {
        this.data = data;
        return this;
    }

    /**
     * Sets the itemstack to glow
     *
     * @param glow if the stack should glow or not
     * @return {@link Button}
     */

    public Button setGlow(boolean glow) {
        this.glow = glow;
        return this;
    }

    /**
     * Sets the enchantments of the stack
     *
     * @param enchantments of the stack
     * @return {@link Button}
     */

    public Button setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    /**
     * Adds enchantment to the stack
     *
     * @param enchantment to add
     * @param value       level of the enchantment
     * @return {@link Button}
     */

    public Button addEnchantment(Enchantment enchantment, int value) {
        this.enchantments.put(enchantment, value);
        return this;
    }

    /**
     * Removes an enchantment from the stack
     *
     * @param enchantment to remove
     * @return {@link Button}
     */

    public Button removeEnchantment(Enchantment enchantment) {
        this.enchantments.remove(enchantment);
        return this;
    }

    /**
     * Convert the button into an {@link ItemStack}.
     *
     * @return the newly created item stack
     */
    public ItemStack toItemStack() {
        final ItemStack item = new ItemStack(this.getMaterial(), this.getAmount(), this.getData());
        final ItemMeta meta;

        if (this.meta == null) {
            meta = item.getItemMeta();
        } else {
            meta = this.meta;
        }

        if (meta != null) { // it can STILL be null, some items don't have an item meta.
            if (this.getDisplayName() != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.getDisplayName()));
            }

            if (this.getLore() != null) {
                meta.setLore(Arrays.stream(this.getLore())
                        .map(string -> ChatColor.translateAlternateColorCodes('&', string))
                        .collect(Collectors.toList())
                );
            }

            if (glow)
                meta.addEnchant(Enchantment.DURABILITY, 1, true);

            if (!enchantments.isEmpty())
                enchantments.forEach((enchantment, integer) -> meta.addEnchant(enchantment, integer, true));

            item.setItemMeta(meta);
        }

        return item;
    }
}