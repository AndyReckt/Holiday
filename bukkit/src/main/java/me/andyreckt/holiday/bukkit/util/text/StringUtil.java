package me.andyreckt.holiday.bukkit.util.text;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

import java.util.*;


public class StringUtil {

     static List<ChatColor> COLORS = new ArrayList<>(Arrays.asList(
            ChatColor.WHITE, ChatColor.GOLD, ChatColor.LIGHT_PURPLE,
            ChatColor.AQUA, ChatColor.YELLOW, ChatColor.GREEN,
            ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.DARK_AQUA,
            ChatColor.DARK_PURPLE, ChatColor.BLUE, ChatColor.BLACK,
            ChatColor.DARK_GREEN, ChatColor.RED));



    public static String formatInteger(int value) {
        return String.format("%,d", value);
    }

    public static int convertChatColorToWoolData(ChatColor color) {
        return color == ChatColor.DARK_RED || color == ChatColor.RED ? 14
                : color == ChatColor.DARK_GREEN ? 13
                : color == ChatColor.BLUE ? 11
                : color == ChatColor.DARK_PURPLE ? 10
                : color == ChatColor.DARK_AQUA ? 9
                : color == ChatColor.DARK_GRAY ? 7
                : COLORS.indexOf(color);
    }

    public static ChatColor convertWoolDataToChatColor(int data) {
        return data == 0 ? ChatColor.WHITE : data == 1 ? ChatColor.GOLD : data == 3 ? ChatColor.AQUA :
                data == 4 ? ChatColor.YELLOW : data == 5 ? ChatColor.GREEN : data == 6 ? ChatColor.LIGHT_PURPLE :
                data == 7 ? ChatColor.DARK_GRAY : data == 8 ? ChatColor.GRAY : data == 9 ? ChatColor.DARK_AQUA :
                data == 10 ? ChatColor.DARK_PURPLE : data == 11 ? ChatColor.BLUE : data == 13 ? ChatColor.DARK_GREEN :
                data == 14 ? ChatColor.RED : data == 15 ? ChatColor.DARK_RED : null;
    }

    public static ChatColor convertDyeDataToChatColor(int data) {
        return data == 0 ? ChatColor.BLACK : data == 1 ? ChatColor.RED : data == 2 ? ChatColor.DARK_GREEN :
                data == 4 ? ChatColor.BLUE : data == 5 ? ChatColor.DARK_PURPLE : data == 6 ? ChatColor.DARK_AQUA :
                data == 7 ? ChatColor.GRAY : data == 8 ? ChatColor.DARK_GRAY : data == 13 ? ChatColor.LIGHT_PURPLE :
                data == 10 ? ChatColor.GREEN : data == 11 ? ChatColor.YELLOW : data == 12 ? ChatColor.AQUA :
                data == 14 ? ChatColor.GOLD : data == 15 ? ChatColor.WHITE : null;
    }

    public static int convertChatColorToDyeData(ChatColor color) {
        return color == ChatColor.BLACK ? 0
                : color == ChatColor.RED ? 1
                : color == ChatColor.DARK_GREEN ? 2
                : color == ChatColor.BLUE ? 4
                : color == ChatColor.DARK_PURPLE ? 5
                : color == ChatColor.DARK_AQUA ? 6
                : color == ChatColor.DARK_GRAY ? 8
                : color == ChatColor.GRAY ? 7
                : color == ChatColor.LIGHT_PURPLE ? 13
                : color == ChatColor.GREEN ? 10
                : color == ChatColor.YELLOW ? 11
                : color == ChatColor.AQUA ? 12
                : color == ChatColor.GOLD ? 14
                : color == ChatColor.WHITE ? 15
                : null;
    }

    public static Enchantment getEnchantmentByName(Object object) {
        String value = object.toString().replace("_", "").trim();

        for(Enchantment enchant : Enchantment.values()) {
            if(value.equals(String.valueOf(enchant.getId()))
                || value.equalsIgnoreCase(enchant.getName().replace("_", ""))
                || value.equalsIgnoreCase(enchant.getName())) {
                return enchant;
            }
        }

        switch (value.toUpperCase()) {
            case "PROT":
            case "PROTECTION": return Enchantment.PROTECTION_ENVIRONMENTAL;
            case "UNB":
            case "UNBREAKING": return Enchantment.DURABILITY;
            case "FIREP":
            case "FP":
            case "FIREPROTECTION": return Enchantment.PROTECTION_FIRE;
            case "FEATHERF":
            case "FL":
            case "FEATHERFALLING": return Enchantment.PROTECTION_FALL;
            case "BLASTP":
            case "BP":
            case "BLASTPROTECTION": return Enchantment.PROTECTION_EXPLOSIONS;
            case "SHARP":
            case "SHARPNESS": return Enchantment.DAMAGE_ALL;
            case "KNOCK":
            case "KNOCKBACK": return Enchantment.KNOCKBACK;
            case "FIREA":
            case "FA":
            case "FIRE":
            case "FIREASPECT": return Enchantment.FIRE_ASPECT;
            case "L":
            case "LOOT":
            case "LOOTING": return Enchantment.LOOT_BONUS_MOBS;
            case "F":
            case "FORT":
            case "FORTUNE": return Enchantment.LOOT_BONUS_BLOCKS;
            case "ST":
            case "SILK":
            case "SILKTOUCH": return Enchantment.SILK_TOUCH;
            case "EFF":
            case "EFFICIENCY": return Enchantment.DIG_SPEED;
            case "SM":
            case "SMITE": return Enchantment.DAMAGE_UNDEAD;
            case "INF":
            case "INFINITY": return Enchantment.ARROW_INFINITE;
            case "FLA":
            case "FLAME": return Enchantment.ARROW_FIRE;
            case "PUNCH": return Enchantment.ARROW_KNOCKBACK;
            case "POWER": return Enchantment.ARROW_DAMAGE;
            default: return null;
        }
    }

    public static String niceBuilder(Collection<String> collection) {
        return niceBuilder(collection, ", ", " and ", ".");
    }

    public static String niceBuilder(Collection<String> collection, String color) {
        return niceBuilder(collection, color + ", ", color + " and ", color + '.');
    }

    public static String niceBuilder(Collection<String> collection, String delimiter, String and, String dot) {
        if(collection != null && !collection.isEmpty()) {
            List<String> contents = new ArrayList<>(collection);

            // removing last object from list
            String last = null;
            if(contents.size() > 1) {
                last = contents.remove(contents.size() - 1);
            }

            StringBuilder builder = new StringBuilder();

            for (String name : contents) {
                if (builder.length() > 0) {
                    builder.append(delimiter);
                }

                builder.append(name);
            }

            if(last != null) {
                builder.append(and).append(last);
            }

            return builder.append(dot != null ? dot : "").toString();
        }

        return "";
    }

    public static int generateRandomNumber(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }


}
