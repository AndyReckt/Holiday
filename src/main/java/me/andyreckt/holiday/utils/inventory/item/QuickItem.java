package me.andyreckt.holiday.utils.inventory.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class QuickItem {
     final ItemStack is;
     Consumer<QuickEvent> consumer;

    public QuickItem(final Material m) {
        this(m, 1);
    }

    public QuickItem(final ItemStack is) {
        this.is = is;
    }

    public QuickItem(final Material m, final int amount) {
        this(m, amount, 0);
    }

    public QuickItem(final Material m, final int amount, final int meta) {
        this.is = new ItemStack(m, amount, (short) meta);
    }

    public QuickItem clone() {
        return new QuickItem(this.is);
    }

    public QuickItem setDurability(final short dur) {
        this.is.setDurability(dur);
        return this;
    }

    public QuickItem glow(final boolean state) {
        if (state) {
            this.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
            final ItemMeta itemMeta = this.is.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            this.is.setItemMeta(itemMeta);
        }
        return this;
    }

    public QuickItem glow() {
        return this.glow(true);
    }

    public String getName() {
        final ItemMeta im = this.is.getItemMeta();
        return im.getDisplayName();
    }

    public QuickItem setName(final String name) {
        final ItemMeta im = this.is.getItemMeta();
        im.setDisplayName(name);
        this.is.setItemMeta(im);
        return this;
    }

    public QuickItem addUnsafeEnchantment(final Enchantment ench, final int level) {
        this.is.addUnsafeEnchantment(ench, level);
        return this;
    }

    public QuickItem removeEnchantment(final Enchantment ench) {
        this.is.removeEnchantment(ench);
        return this;
    }

    public QuickItem setSkullOwner(final String owner) {
        try {
            final SkullMeta im = (SkullMeta) this.is.getItemMeta();
            im.setOwner(owner);
            this.is.setItemMeta(im);
        } catch (ClassCastException ex) {
        }
        return this;
    }

    public QuickItem addEnchant(final Enchantment ench, final int level, final boolean show) {
        final ItemMeta im = this.is.getItemMeta();
        im.addEnchant(ench, level, show);
        this.is.setItemMeta(im);
        return this;
    }

    public Map<Enchantment, Integer> getEnchants() {
        final ItemMeta im = this.is.getItemMeta();
        return im.getEnchants();
    }

    public QuickItem addItemFlag(final ItemFlag... itemFlags) {
        final ItemMeta im = this.is.getItemMeta();
        im.addItemFlags(itemFlags);
        this.is.setItemMeta(im);
        return this;
    }

    public QuickItem hideEnchant() {
        final ItemMeta im = this.is.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        this.is.setItemMeta(im);
        return this;
    }

    public QuickItem setInfinityDurability() {
        this.is.setDurability((short) (-32768));
        return this;
    }

    public List<String> getLore() {
        return this.is.getItemMeta().getLore();
    }

    public QuickItem setLore(final String... lore) {
        return this.setLore(Arrays.asList(lore));
    }

    public QuickItem setLore(final List<String> lore) {
        final ItemMeta im = this.is.getItemMeta();
        im.setLore(lore);
        this.is.setItemMeta(im);
        return this;
    }

    public Color getLeatherArmorColor() {
        final LeatherArmorMeta im = (LeatherArmorMeta) this.is.getItemMeta();
        return im.getColor();
    }

    public QuickItem setLeatherArmorColor(final Color color) {
        try {
            final LeatherArmorMeta im = (LeatherArmorMeta) this.is.getItemMeta();
            im.setColor(color);
            this.is.setItemMeta(im);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return this;
    }

    public QuickItem setTexture(final String hash) {
        final GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        final PropertyMap propertyMap = profile.getProperties();
        propertyMap.put("textures", new Property("textures", hash));
        final SkullMeta skullMeta = (SkullMeta) this.is.getItemMeta();
        final Class<?> c_skullMeta = skullMeta.getClass();
        try {
            final Field f_profile = c_skullMeta.getDeclaredField("profile");
            f_profile.setAccessible(true);
            f_profile.set(skullMeta, profile);
            f_profile.setAccessible(false);
            this.is.setItemMeta(skullMeta);
            return this;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return this;
    }

    public QuickItem onClick(final Consumer<QuickEvent> consumer) {
        this.consumer = consumer;
        return this;
    }

    public ItemStack toItemStack() {
        if (this.consumer != null) {
            QuickItemManager.registerItem(this, this.is, this.consumer);
        }
        return this.is;
    }
}
