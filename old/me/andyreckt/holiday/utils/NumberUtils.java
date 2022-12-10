package me.andyreckt.holiday.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Marko on 02.03.2019.
 */
public class NumberUtils {

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isShort(String input) {
        try {
            Short.parseShort(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int generateRandomIntInRange(int min, int max) {
        Random r = ThreadLocalRandom.current();
        return r.nextInt((max - min) + 1) + min;
    }
}
