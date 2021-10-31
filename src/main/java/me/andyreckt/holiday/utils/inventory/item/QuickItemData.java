package me.andyreckt.holiday.utils.inventory.item;

import org.bukkit.*;
import org.bukkit.inventory.meta.*;

public class QuickItemData
{
     Material material;
     byte data;
     ItemMeta itemMeta;
    
    public QuickItemData(final Material material, final byte data, final ItemMeta itemMeta) {
        this.material = material;
        this.data = data;
        this.itemMeta = itemMeta;
    }
    
    public Material getMaterial() {
        return this.material;
    }
    
    public byte getData() {
        return this.data;
    }
    
    public ItemMeta getItemMeta() {
        return this.itemMeta;
    }
}
