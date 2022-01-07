package me.andyreckt.holiday.player.grant;

import java.util.Comparator;

/**
 * This Class is from Zowpy
 * All credits to him
 *
 * @author Zowpy
 */

public class GrantComparator implements Comparator<Grant> {

    @Override
    public int compare(Grant o1, Grant o2) {
        return Integer.compare(o1.getPriority(), o2.getPriority());
    }
}