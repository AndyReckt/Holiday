package me.andyreckt.holiday.bukkit.util.menu.buttons;

import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.text.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ListChoiceButton<T> extends Button {
    private final ItemStack item;
    private final T target;
    private final Consumer<T> writeFunction;
    private final Supplier<T> readFunction;
    private final Consumer<T> saveFunction;
    private final List<String> traits;

    public ListChoiceButton(ItemStack item, T target, Consumer<T> writeFunction, Supplier<T> readFunction, Consumer<T> saveFunction, String... traits) {
        this(item, target, writeFunction, readFunction, saveFunction, Arrays.asList(traits));
    }

    public ListChoiceButton(ItemStack item, T target, Consumer<T> toDo, Supplier<T> getFrom, Consumer<T> saveFunction, List<String> traits) {
        this.item = item;
        this.target = target;
        this.writeFunction = toDo;
        this.readFunction = getFrom;
        this.saveFunction = saveFunction;
        this.traits = traits;
    }

    @Override
    public ItemStack getButtonItem(Player p0) {
        T current = this.readFunction.get();

        List<String> focus = new ArrayList<>();
        for (String trait : traits) {
            focus.add(line(trait.equals(current.toString()) ? CC.GREEN + trait : CC.WHITE + trait));
        } //TODO: verify how to work with this

        return new ItemBuilder(item)
                .lore("")
                .lore(focus.toArray(new String[0]))
                .lore("")
                .lore(CC.I_GRAY + "Click to switch.")
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        this.writeFunction.accept(this.target);
        this.saveFunction.accept(this.target);
    }

    private String line(String s) {
        return CC.B_PRIMARY + CC.LINE + " " + s;
    }
}
