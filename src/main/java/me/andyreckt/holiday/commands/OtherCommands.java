package me.andyreckt.holiday.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.buttons.DisplayButton;
import io.github.zowpy.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.server.Server;
import me.andyreckt.holiday.server.nms.impl.NMS_v1_8;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.StringUtils;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class OtherCommands {

    @Command(names = {"garbage"}, perm = "holiday.garbage", async = true)
    public static void gc(CommandSender sender) {
        System.gc();
        sender.sendMessage(CC.translate("&aSuccessfully ran the garbage collector."));
    }

    @Command(names = "fly", perm = "holiday.fly")
    public static void fly(Player sender) {
        sender.setAllowFlight(!sender.getAllowFlight());
        if (sender.getAllowFlight()) {
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.FLY.ENABLED"));
        } else {
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.FLY.DISABLED"));
        }
    }

    @Command(names = {"give"}, perm = "holiday.give")
    public void give(Player sender, @Param(name = "player") Player target, @Param(name = "material") String material, @Param(name = "amount") int amount) {
        Material mat = Bukkit.getUnsafe().getMaterialFromInternalName(material);
        if (mat != null) {
            ItemStack Item = new ItemStack(mat, amount);
            PlayerInventory inv = target.getInventory();
            inv.addItem(Item);
            target.updateInventory();
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.GIVE.SENDER")
                    .replace("<material>", mat.name()).replace("<amount>", String.valueOf(amount)).replace("<target>", target.getName()));
            target.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.GIVE.TARGET")
                    .replace("<material>", mat.name()).replace("<amount>", String.valueOf(amount)).replace("<player>", sender.getName()));
        }
    }

    @Command(names = {"giveall"}, perm = "holiday.giveall")
    public static void giveall(Player sender, @Param(name = "material") String material, @Param(name = "amount") int amount) {
        Material mat = Bukkit.getUnsafe().getMaterialFromInternalName(material);
        if (mat != null) {
            ItemStack Item = new ItemStack(mat, amount);
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerInventory inv = player.getInventory();
                inv.addItem(Item);
                player.updateInventory();
            }
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.GIVEALL.SENDER")
                    .replace("<material>", mat.name()).replace("<amount>", String.valueOf(amount)));
            Holiday.getInstance().getProfileHandler().getOnlineProfiles().stream()
                    .map(Profile::getPlayer)
                    .forEach(player ->
                            player.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.GIVEALL.BROADCAST")
                                    .replace("<material>", mat.name())
                                    .replace("<amount>", String.valueOf(amount))
                                    .replace("<player>", Holiday.getInstance().getProfileHandler().getByPlayer(sender).getDisplayNameWithColor())));
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
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.GIVE.YOURSELF")
                    .replace("<material>", mat.name()).replace("<amount>", String.valueOf(amount)));
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
                player.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.CLEAR.YOURSELF"));
            } else {
                clearPlayer(target);
                target.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.CLEAR.TARGET").replace("<player>", sender.getName()));
                player.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.CLEAR.SENDER").replace("<player>", target.getName()));
            }
        } else {
            clearPlayer(target);
            target.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.CLEAR.TARGET").replace("<player>", "&4CONSOLE"));
        }

    }

    @Command(names = "heal", perm = "holiday.heal")
    public static void heal(CommandSender sender, @Param(name = "target", defaultValue = "self") Player target) {
        target.setHealth(target.getMaxHealth());
        if (sender == target) {
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.HEAL.YOURSELF"));
        } else {
            target.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.HEAL.TARGET").replace("<player>", sender.getName()));
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.HEAL.SENDER").replace("<player>", target.getName()));
        }
    }

    @Command(names = "feed", perm = "holiday.feed")
    public static void feed(CommandSender sender, @Param(name = "target", defaultValue = "self") Player target) {
        target.setSaturation(20);
        target.setFoodLevel(20);
        if (sender == target) {
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.FEED.YOURSELF"));
        } else {
            target.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.FEED.TARGET").replace("<player>", sender.getName()));
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.FEED.SENDER").replace("<player>", target.getName()));
        }
    }

    @Command(names = {"enchant"}, perm = "holiday.enchant")
    public static void enchant(Player sender, @Param(name = "enchantment") String enchant, @Param(name = "level") int level) {
        ItemStack item = sender.getItemInHand();

        if(item == null || item.getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cYou need to have the item in your hand."));
            return;
        }

        if(level < 0) {
            sender.sendMessage(CC.translate("&cThe level must be equal or greater than 0."));
            return;
        }

        if (level > 10 && !sender.isOp()) {
            sender.sendMessage(CC.translate("&cThe level cant be greater than 10."));
            return;
        }


        String enchantment = StringUtils.getEnchantment(enchant);

        if(level == 0) {
            if(item.containsEnchantment(Enchantment.getByName(enchantment))) {
                item.removeEnchantment(Enchantment.getByName(enchantment));

                sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.ENCHANT.REMOVED").replace("<enchant>", enchant.toUpperCase()));
            } else {
                sender.sendMessage(CC.translate("&cThis item does not contain the enchantement " + enchant.toUpperCase() + "&c."));
            }
        } else {
            item.addUnsafeEnchantment(Enchantment.getByName(enchantment), level);

            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.ENCHANT.ADDED").replace("<item>", item.getType().name()));
        }
    }

    @Command(names = {"demomode", "demo"}, perm = "holiday.demomode")
    public static void demo(CommandSender sender, @Param(name = "player", defaultValue = "self") Player target) {
        if (!Holiday.getInstance().isProtocolEnabled()) {
            sender.sendMessage(CC.translate("&cYou need ProtocolLib in order to run this command"));
            return;
        }

        if (!(Holiday.getInstance().getNmsHandler() instanceof NMS_v1_8)) {
            sender.sendMessage(CC.translate("&cYou need to be in 1.8 in order to use this command for compatibility reasons"));
            return; //TODO Add this to NMS handler so its usable in 1.7 too
        }

        final PacketContainer packet = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        packet.getIntegers().write(0, 5);
        packet.getFloat().write(0, 0.0f);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, packet);
            sender.sendMessage(CC.CHAT + "Successfully sent the demo screen to " + CC.PRIMARY + target.getName());
        } catch (Exception ignored) {
            sender.sendMessage(CC.translate("&cFailed to send the demo screen to that player."));
        }
    }


    private static void clearPlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }






}
