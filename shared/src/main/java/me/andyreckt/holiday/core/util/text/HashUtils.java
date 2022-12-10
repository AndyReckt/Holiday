package me.andyreckt.holiday.core.util.text;

public class HashUtils {
    public static String hash(String input) {
        int hash = 7;
        for (int i = 0; i < input.length(); i++) {
            hash = hash * 31 + input.charAt(i);
        }
        return String.valueOf(hash);
    }
}
