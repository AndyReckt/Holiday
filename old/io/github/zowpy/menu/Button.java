package io.github.zowpy.menu;

import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.apache.commons.lang.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.*;
import org.bukkit.event.inventory.*;

public abstract class Button
{
    public static Button placeholder(final Material material, final byte data, final String... title) {
        return new Button() {
            @Override
            public ItemStack getButtonItem(final Player player) {
                final ItemStack it = new ItemStack(material, 1, (short)data);
                final ItemMeta meta = it.getItemMeta();
                meta.setDisplayName(StringUtils.join((Object[])title));
                it.setItemMeta(meta);
                return it;
            }
        };
    }
    
    public static void playFail(final Player player) {
        player.playSound(player.getLocation(), Sound.DIG_GRASS, 20.0f, 0.1f);
    }
    
    public static void playSuccess(final Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20.0f, 15.0f);
    }
    
    public static void playNeutral(final Player player) {
        player.playSound(player.getLocation(), Sound.CLICK, 20.0f, 1.0f);
    }
    
    public abstract ItemStack getButtonItem(final Player p0);
    
    public void clicked(final Player player, final int slot, final ClickType clickType, final int hotbarButton) {
    }
    
    public boolean shouldCancel(final Player player, final int slot, final ClickType clickType) {
        return true;
    }
    
    public boolean shouldUpdate(final Player player, final int slot, final ClickType clickType) {
        return false;
    }
    
    public static void playSound(final Player player, final ButtonSound buttonSound) {
        player.playSound(player.getLocation(), buttonSound.getSound(), 20.0f, 1.0f);
    }
}
