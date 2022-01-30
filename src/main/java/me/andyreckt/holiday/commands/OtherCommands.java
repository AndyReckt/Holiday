package me.andyreckt.holiday.commands;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.buttons.DisplayButton;
import io.github.zowpy.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class OtherCommands {

    private static final BasicConfigurationFile messages = Holiday.getInstance().getMessages();

    @Command(names = {"garbage"}, perm = "holiday.garbage", async = true)
    public static void gc(CommandSender sender) {
        System.gc();
        sender.sendMessage(CC.translate("&aSuccessfully ran the garbage collector."));
    }

    @Command(names = "fly", perm = "holiday.fly")
    public static void fly(Player sender) {
        sender.setAllowFlight(!sender.getAllowFlight());
        sender.sendMessage(sender.getAllowFlight() ? messages.getString("COMMANDS.GENERAL.FLY.ON") : messages.getString("COMMANDS.GENERAL.FLY.OFF"));
    }

    @Command(names = {"give"}, perm = "holiday.give")
    public void give(Player sender, @Param(name = "player") Player target, @Param(name = "material") String material, @Param(name = "amount") int amount) {
        Material mat = Bukkit.getUnsafe().getMaterialFromInternalName(material);
        if (mat != null) {
            ItemStack Item = new ItemStack(mat, amount);
            PlayerInventory inv = target.getInventory();
            inv.addItem(Item);
            target.updateInventory();
            sender.sendMessage(messages.getString("COMMANDS.GENERAL.GIVE.SENDER")
                    .replace("<material>", material).replace("<amount>", String.valueOf(amount)).replace("<target>", target.getName()));
            target.sendMessage(messages.getString("COMMANDS.GENERAL.GIVE.TARGET")
                    .replace("<material>", material).replace("<amount>", String.valueOf(amount)).replace("<player>", sender.getName()));
        }
    }

    @Command(names = {"giveme", "i", "gimme"}, perm = "holiday.give")
    public static void giveme(Player sender, @Param(name = "material") String material, @Param(name = "amount") int amount) {
        Material mat = Bukkit.getUnsafe().getMaterialFromInternalName(material);
        if (mat != null) {
            ItemStack Item = new ItemStack(mat, amount);
            PlayerInventory inv = sender.getInventory();
            inv.addItem(Item);
            sender.updateInventory();
            sender.sendMessage(messages.getString("COMMANDS.GENERAL.GIVE.YOURSELF")
                    .replace("<material>", material).replace("<amount>", String.valueOf(amount)));
        }
    }

    @Command(names = {"craft", "workbench"}, perm = "holiday.workbench")
    public static void craft(Player sender) {
        sender.openWorkbench(sender.getLocation(), true);
    }

    @Command(names = "clear", perm = "holiday.clear")
    public static void clear(CommandSender sender, @Param(name = "player", defaultValue = "self") Player target) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(target == player) {
                clearPlayer(player);
                player.sendMessage(messages.getString("COMMANDS.GENERAL.CLEAR.YOURSELF"));
            } else {
                clearPlayer(target);
                target.sendMessage(messages.getString("COMMANDS.GENERAL.CLEAR.TARGET").replace("<player>", sender.getName()));
                player.sendMessage(messages.getString("COMMANDS.GENERAL.CLEAR.SENDER").replace("<player>", target.getName()));
            }
        } else {
            clearPlayer(target);
            target.sendMessage(messages.getString("COMMANDS.GENERAL.CLEAR.TARGET").replace("<player>", "&4CONSOLE"));
        }

    }

    @Command(names = "heal", perm = "holiday.heal")
    public static void heal(CommandSender sender, @Param(name = "target", defaultValue = "self") Player target) {
        target.setHealth(target.getMaxHealth());
        if (sender == target) {
            sender.sendMessage(messages.getString("COMMANDS.GENERAL.HEAL.YOURSELF"));
        } else {
            target.sendMessage(messages.getString("COMMANDS.GENERAL.HEAL.TARGET").replace("<player>", sender.getName()));
            sender.sendMessage(messages.getString("COMMANDS.GENERAL.HEAL.SENDER").replace("<player>", target.getName()));
        }
    }

    @Command(names = "feed", perm = "holiday.feed")
    public static void feed(CommandSender sender, @Param(name = "target", defaultValue = "self") Player target) {
        target.setSaturation(20);
        target.setFoodLevel(20);
        if (sender == target) {
            sender.sendMessage(messages.getString("COMMANDS.GENERAL.FEED.YOURSELF"));
        } else {
            target.sendMessage(messages.getString("COMMANDS.GENERAL.FEED.TARGET").replace("<player>", sender.getName()));
            sender.sendMessage(messages.getString("COMMANDS.GENERAL.FEED.SENDER").replace("<player>", target.getName()));
        }
    }


    private static void clearPlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

}
