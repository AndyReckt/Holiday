package me.andyreckt.holiday.bukkit.util.menu.buttons;

import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.text.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class BooleanButton<T> extends Button {
    private ItemStack item;
    private final T target;
    private final String trait;
    private final BiConsumer<T, Boolean> writeFunction;
    private final Function<T, Boolean> readFunction;
    private final Consumer<T> saveFunction;

    public BooleanButton(ItemStack item, T target, String trait, BiConsumer<T, Boolean> writeFunction, Function<T, Boolean> readFunction) {
        this(item, target, trait, writeFunction, readFunction, (i) -> {});
    }

    public BooleanButton(T target, String trait, BiConsumer<T, Boolean> writeFunction, Function<T, Boolean> readFunction) {
        this(target, trait, writeFunction, readFunction, i -> {});
    }

    public BooleanButton(T target, String trait, BiConsumer<T, Boolean> toDo, Function<T, Boolean> getFrom, Consumer<T> saveFunction) {
        this.target = target;
        this.trait = trait;
        this.writeFunction = toDo;
        this.readFunction = getFrom;
        this.saveFunction = saveFunction;
    }

    public BooleanButton(ItemStack item, T target, String trait, BiConsumer<T, Boolean> toDo, Function<T, Boolean> getFrom, Consumer<T> saveFunction) {
        this.item = item;
        this.target = target;
        this.trait = trait;
        this.writeFunction = toDo;
        this.readFunction = getFrom;
        this.saveFunction = saveFunction;
    }


    @Override
    public ItemStack getButtonItem(Player p0) {
        boolean current = this.readFunction.apply(this.target);
        return item == null ? new ItemBuilder(Material.WOOL)
                .displayname((this.readFunction.apply(this.target) ? "&a" : "&c") + this.trait)
                .damage(this.readFunction.apply(this.target) ? 5 : 14)
                .build() :
                new ItemBuilder(item)
                        .lore("",
                            current  ? CC.B_PRIMARY + CC.LINE + " " + CC.GREEN + "Enabled"
                                     : CC.B_PRIMARY + CC.LINE + " " + CC.WHITE + "Enabled",
                            !current ? CC.B_PRIMARY + CC.LINE + " " + CC.RED + "Disabled"
                                     : CC.B_PRIMARY + CC.LINE + " " + CC.WHITE + "Disabled"
                        ).build();
//                        CC.CHAT + "Enabled: " + yesNo(this.readFunction.apply(this.target))).build();
    }

    private String yesNo(boolean apply) {
        return apply ? CC.GREEN + "Yes" : CC.RED + "No";
    }

    @Override
    public void clicked(final Player player, final int i, final ClickType clickType, final int hb) {
        boolean current = this.readFunction.apply(this.target);
        this.writeFunction.accept(this.target, !current);
        this.saveFunction.accept(this.target);
//        player.sendMessage(ChatColor.GREEN + "Changed " + this.trait + " to " + (current ? "disabled" : "enabled"));
    }
}