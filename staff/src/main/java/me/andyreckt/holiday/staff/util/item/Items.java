package me.andyreckt.holiday.staff.util.item;

import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.text.CC;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public enum Items {

    INVSEE(new ItemBuilder(Material.BOOK).displayname(CC.CHAT + "Inspect Player").build(),
            event -> {
                if (!(event.getRightClicked() instanceof Player)) return;
                Player target = (Player) event.getRightClicked();
                event.getPlayer().performCommand("invsee " + target.getName());
            }, null),
    GO_INVISIBLE(new ItemBuilder(Material.INK_SACK).displayname(CC.CHAT + "Go Invisible").damage(8).build(),
            event -> event.getPlayer().performCommand("vanish")),
    GO_VISIBLE(new ItemBuilder(Material.INK_SACK).displayname(CC.CHAT + "Go Visible").damage(10).build(),
            event -> event.getPlayer().performCommand("vanish")),
    BETTER_VIEW(new ItemBuilder(Material.CARPET).displayname(CC.CHAT + "Better View").damage(4).build(), null),
    FREEZE(new ItemBuilder(Material.PACKED_ICE).displayname(CC.CHAT + "Freeze").build(),
            event -> {
                if (!(event.getRightClicked() instanceof Player)) return;
                Player target = (Player) event.getRightClicked();
                event.getPlayer().performCommand("freeze " + target.getName());
            }, null),
    STAFF_LIST(new ItemBuilder(Material.SKULL_ITEM).displayname(CC.CHAT + "Staff List").damage(SkullType.PLAYER.ordinal()).build(),
            event -> event.getPlayer().performCommand("stafflist")),
    ;

    private final ItemStack itemStack;
    private Consumer<PlayerInteractEvent> interactEvent;
    private Consumer<PlayerInteractEntityEvent> interactEntityEvent;

    Items(ItemStack itemStack, Consumer<PlayerInteractEvent> event) {
        this.itemStack = itemStack;
        this.interactEvent = event;
    }

    Items(ItemStack itemStack, Consumer<PlayerInteractEntityEvent> event, Object o) {
        this.itemStack = itemStack;
        this.interactEntityEvent = event;
    }

    public ItemStack getItem() {
        return itemStack;
    }

    public void accept(PlayerInteractEvent event) {
        if(interactEvent != null) {
            interactEvent.accept(event);
        }
    }

    public void accept(PlayerInteractEntityEvent event) {
        if(interactEntityEvent != null) {
            interactEntityEvent.accept(event);
        }
    }

    public Event getEvent() {
        if (interactEvent != null) {
            return new PlayerInteractEvent(null, null, null, null, null);
        } else if (interactEntityEvent != null) {
            return new PlayerInteractEntityEvent(null, null);
        }
        return null;
    }



}
