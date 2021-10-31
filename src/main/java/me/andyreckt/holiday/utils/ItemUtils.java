package me.andyreckt.holiday.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemUtils {

     static final Map<String, ItemData> NAME_MAP = new HashMap<>();

    public static ItemData[] repeat(Material material, int times) {
        return repeat(material, (byte) 0, times);
    }

    public static ItemData[] repeat(Material material, byte data, int times) {
        ItemData[] itemData = new ItemData[times];

        for(int i = 0; i < times; i++) {
            itemData[i] = new ItemData(material, data);
        }

        return itemData;
    }

    public static ItemData[] armorOf(ArmorPart part) {
        List<ItemData> data = new ArrayList<>();

        for(ArmorType at : ArmorType.values()) {
            data.add(new ItemData(Material.valueOf(at.name() + "_" + part.name()), (short) 0));
        }

        return data.toArray(new ItemData[data.size()]);
    }

    public static ItemData[] swords() {
        List<ItemData> data = new ArrayList<>();

        for(SwordType at : SwordType.values()) {
            data.add(new ItemData(Material.valueOf(at.name() + "_SWORD"), (short) 0));
        }

        return data.toArray(new ItemData[data.size()]);
    }


    public static ItemStack get(String input, int amount) {
        ItemStack item = get(input);

        if(item != null) item.setAmount(amount);

        return item;
    }

    public static ItemStack get(String input) {
        if(NumberUtils.isInteger(input)) {
            return new ItemStack(Material.getMaterial(Integer.parseInt(input)));
        }

        if(input.contains(":")) {
            if(NumberUtils.isShort(input.split(":")[1])) {
                if(NumberUtils.isInteger(input.split(":")[0])) {
                    return new ItemStack(Material.getMaterial(Integer.parseInt(input.split(":")[0])), 1, Short.parseShort(input.split(":")[1]));
                } else {
                    if(!NAME_MAP.containsKey(input.split(":")[0].toLowerCase())) {
                        return null;
                    }

                    ItemData data = NAME_MAP.get(input.split(":")[0].toLowerCase());
                    return new ItemStack(data.getMaterial(), 1, Short.parseShort(input.split(":")[1]));
                }
            } else {
                return null;
            }
        }

        if(!NAME_MAP.containsKey(input)) {
            return null;
        }

        return NAME_MAP.get(input).toItemStack();
    }

    public static String getName(ItemStack item) {
        if(item.getDurability() != 0) {
            net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);

            if(nmsStack != null) {
                String name = nmsStack.getName();

                if(name.contains(".")) {
                    name = WordUtils.capitalize(item.getType().toString().toLowerCase().replace("_", " "));
                }

                return name;
            }
        }

        String string = item.getType().toString().replace("_", " ");
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for(int i = 0; i < chars.length; i++) {
            if(!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if(Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
                found = false;
            }
        }

        return String.valueOf(chars);
    }

    @Getter
    @AllArgsConstructor
    public static class ItemData {

         final Material material;
         final short data;

        public String getName() {
            return ItemUtils.getName(toItemStack());
        }

        public boolean matches(ItemStack item) {
            return item != null && item.getType() == material && item.getDurability() == data;
        }

        public ItemStack toItemStack() {
            return new ItemStack(material, 1, data);
        }

    }

    public enum ArmorPart {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS
    }

    public enum ArmorType {
        DIAMOND, IRON, GOLD, LEATHER
    }

    public enum SwordType {
        DIAMOND, IRON, GOLD, STONE
    }
}
