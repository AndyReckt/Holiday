package me.andyreckt.holiday.player.staff;

import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;

public class Items {

    public static ItemStack INVSEE;
    public static ItemStack GO_VIS;
    public static ItemStack GO_INVIS;
    public static ItemStack FREEZE;
    public static ItemStack BETTER_VIEW;
    public static ItemStack STAFF_LIST;

    static {
        INVSEE = new ItemBuilder(Material.BOOK).displayname(CC.CHAT + "Inspect Player").build();
        GO_INVIS = new ItemBuilder(Material.INK_SACK).displayname(CC.CHAT + "Vanish").damage(8).build();
        GO_VIS = new ItemBuilder(Material.INK_SACK).displayname(CC.CHAT + "Unvanish").damage(10).build();
        FREEZE = new ItemBuilder(Material.PACKED_ICE).displayname(CC.CHAT + "Freeze").build();
        BETTER_VIEW = new ItemBuilder(Material.CARPET).displayname(CC.CHAT + "Better View").damage(4).build();
        STAFF_LIST = new ItemBuilder(Material.SKULL_ITEM).displayname(CC.CHAT + "Staff List").damage(SkullType.PLAYER.ordinal()).build();
    }

}
