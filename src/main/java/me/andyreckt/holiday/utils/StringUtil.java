package me.andyreckt.holiday.utils;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

/**
 * Created by Marko on 25.02.2019.
 */
public class StringUtil {

     static List<ChatColor> COLORS = new ArrayList<>(Arrays.asList(
            ChatColor.WHITE, ChatColor.GOLD, ChatColor.LIGHT_PURPLE,
            ChatColor.AQUA, ChatColor.YELLOW, ChatColor.GREEN,
            ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.DARK_AQUA,
            ChatColor.DARK_PURPLE, ChatColor.BLUE, ChatColor.BLACK,
            ChatColor.DARK_GREEN, ChatColor.RED));

    public static final char NICE_CHAR = '●';
    public static final char HEART = '\u2764';

    public static final String SERVER_NAME = "Mandown Network";
    public static final String WEBSITE = "www.mandown.us";
    public static final String FACEBOOK = "www.facebook.com/MandownNetwork";
    public static final String STORE_LINK = "store.mandown.us";
    public static final String TEAMSPEAK = "ts.mandown.us";
    public static final String APPEAL = "discord.mandown.us";
    public static final String DISCORD = "discord.mandown.us";
    public static final String RULES = "discord.mandown.us";
    public static final String TWITTER = "www.twitter.com/MandownUS";
    public static final String TWITTER_GAME_FEED = "www.twitter.com/MandownUS";

    public static final String NO_PERMISSION = CC.RED + "No permission.";
    public static final String FOR_PLAYER_ONLY = CC.RED + "Only players can perform this command.";
    public static final String NO_PLAYER_FOUND = CC.RED + "No player with the name '<player>' found.";
    public static final String NO_ITEM_FOUND = CC.RED + "No item with the name '<item>' found.";
    public static final String NO_WORLD_FOUND = CC.RED + "No world with the name '<world>' found.";
    public static final String INTEGER_NOT_VALID = CC.RED + "<source> isn't a valid number.";

    public static final String LOAD_ERROR_1 = CC.RED + "Error found while loading your data. (1)\n\nTry again later or contact a staff member.";
    public static final String LOAD_ERROR_2 = CC.RED + "Error found while loading your data. (2)\n\nTry again later or contact a staff member.";
    public static final String LOAD_ERROR_3 = CC.RED + "Error found while loading your data. (3)\n\nTry again later or contact a staff member.";

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
            Iterator<String> iterator = contents.iterator();

            while (iterator.hasNext()) {
                String name = iterator.next();

                if(builder.length() > 0) {
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

    public static String getSexyTime(long millis) {
        long seconds = millis / 1000L;

        if (seconds <= 0) {
            return "0 seconds";
        }

        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        long day = hours / 24;
        hours = hours % 24;
        long years = day / 365;
        day = day % 365;

        StringBuilder time = new StringBuilder();

        if (years != 0) {
            time.append(years).append(years == 1 ? " year" : " years").append(day == 0 ? " " : ", ");
        }

        if (day != 0) {
            time.append(day).append(day == 1 ? " day" : " days").append(hours == 0 ? " " : ", ");
        }

        if (hours != 0) {
            time.append(hours).append(hours == 1 ? " hour" : " hours").append(minutes == 0 ? " " : ", ");
        }

        if (minutes != 0) {
            time.append(minutes).append(minutes == 1 ? " minute" : " minutes").append(seconds == 0 ? " " : ", ");
        }

        if (seconds != 0) {
            time.append(seconds).append(seconds == 1 ? " second" : " seconds");
        }
        return time.toString().trim();
    }
}
