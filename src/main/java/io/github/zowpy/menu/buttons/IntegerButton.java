package io.github.zowpy.menu.buttons;

import com.google.common.collect.ImmutableList;

import io.github.zowpy.menu.Button;

import me.andyreckt.holiday.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class IntegerButton<T> extends Button {

    private final T target;
    private final String trait;
    private final BiConsumer<T, Integer> writeFunction;
    private final Function<T, Integer> readFunction;
    private final Consumer<T> saveFunction;

    public IntegerButton(T target, String trait, BiConsumer<T, Integer> writeFunction, Function<T, Integer> readFunction) {
        this(target, trait, writeFunction, readFunction, (i) -> {});
    }

    public IntegerButton(T target, String trait, BiConsumer<T, Integer> writeFunction, Function<T, Integer> readFunction, Consumer<T> saveFunction) {
        this.target = target;
        this.trait = trait;
        this.writeFunction = writeFunction;
        this.readFunction = readFunction;
        this.saveFunction = saveFunction;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        int current = readFunction.apply(target);
        int change = clickType.isShiftClick() ? 10 : 1;

        if (clickType.isRightClick()) {
            change = -change;
        }

        writeFunction.accept(target, current + change);
        saveFunction.accept(target);

        player.sendMessage(ChatColor.GREEN + "Set " + trait + " trait to " + (current + change));
    }

    @Override
    public ItemStack getButtonItem(Player p0) {
        return new ItemBuilder(Material.EMERALD)
                .displayname(ChatColor.GOLD + "Edit " + trait)
                .lore(ImmutableList.of(
                                ChatColor.YELLOW + "Current: " + ChatColor.WHITE + readFunction.apply(target),
                                "",
                                ChatColor.GREEN.toString() + ChatColor.BOLD + "LEFT-CLICK " + ChatColor.GREEN + "to increase by 1",
                                ChatColor.GREEN.toString() + ChatColor.BOLD + "SHIFT LEFT-CLICK " + ChatColor.GREEN + "to increase by 10",
                                "",
                                ChatColor.RED.toString() + ChatColor.BOLD + "RIGHT-CLICK " + ChatColor.GREEN + "to decrease by 1",
                                ChatColor.RED.toString() + ChatColor.BOLD + "SHIFT RIGHT-CLICK " + ChatColor.GREEN + "to decrease by 10"))
                .amount(readFunction.apply(target))
                .build();
    }
}