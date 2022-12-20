package me.andyreckt.holiday.bukkit.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.*;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.text.StringUtils;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class OtherCommands { //TODO: add staff broadcasts

    @Command(names = {"garbage"}, permission = Perms.GARBAGE, async = true)
    public void gc(CommandSender sender) {
        System.gc();
        sender.sendMessage(CC.translate("&aSuccessfully ran the garbage collector."));
    }

    @Command(names = "fly", permission = Perms.FLY)
    public void fly(Player sender) {
        sender.setAllowFlight(!sender.getAllowFlight());
        String executor = Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId()));
        if (sender.getAllowFlight()) {
            sender.sendMessage(Locale.FLY_ENABLED.getString());
            String toSend = Locale.FLY_ENABLED_STAFF.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%executor%", executor);
            Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(
                    toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE
            ));
        } else {
            String toSend = Locale.FLY_DISABLED_STAFF.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%executor%", sender.getName());
            Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(
                    toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE
            ));
            sender.sendMessage(Locale.FLY_DISABLED.getString());
        }
    }

    @Command(names = {"give"}, permission = Perms.GIVE)
    public void give(Player sender, @Param(name = "player") Player target, @Param(name = "material") String material, @Param(name = "amount") int amount) {
        Material mat = Bukkit.getUnsafe().getMaterialFromInternalName(material);
        if (mat != null) {
            String executor = Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId()));
            ItemStack Item = new ItemStack(mat, amount);
            PlayerInventory inv = target.getInventory();
            inv.addItem(Item);
            target.updateInventory();
            String x = Locale.GIVE_SENDER.getString()
                    .replace("%player%", target.getName())
                    .replace("%material%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            sender.sendMessage(CC.translate(x));
            String y = Locale.GIVE_TARGET.getString()
                    .replace("%player%", sender.getName())
                    .replace("%material%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            target.sendMessage(CC.translate(y));

            String toSend = Locale.GIVE_STAFF.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%executor%", executor)
                    .replace("%player%", target.getName())
                    .replace("%material%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(
                    toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE
            ));
        } else {
            sender.sendMessage(Locale.INVALID_MATERIAL.getString());
        }
    }

    @Command(names = {"giveall"}, permission = Perms.GIVEALL)
    public void giveall(Player sender, @Param(name = "material") String material, @Param(name = "amount") int amount) {
        Material mat = Bukkit.getUnsafe().getMaterialFromInternalName(material);
        String executor = Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId()));
        if (mat != null) {
            ItemStack Item = new ItemStack(mat, amount);
            String x = Locale.GIVE_ALL.getString()
                    .replace("%material%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            String y = Locale.GIVE_ALL_BROADCAST.getString()
                    .replace("%player%", sender.getName())
                    .replace("%material%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            sender.sendMessage(x);
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerInventory inv = player.getInventory();
                inv.addItem(Item);
                player.updateInventory();
                player.sendMessage(y);
            }
            String toSend = Locale.GIVE_ALL_STAFF.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%executor%", executor)
                    .replace("%material%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(
                    toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE
            ));
        } else {
            sender.sendMessage(Locale.INVALID_MATERIAL.getString());
        }
    }

    @Command(names = {"giveme", "i", "gimme"}, permission = Perms.GIVE)
    public void giveme(Player sender, @Param(name = "material") String material, @Param(name = "amount") int amount) {
        Material mat = Bukkit.getUnsafe().getMaterialFromInternalName(material);
        if (mat != null) {
            String executor = Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId()));
            ItemStack Item = new ItemStack(mat, amount);
            PlayerInventory inv = sender.getInventory();
            inv.addItem(Item);
            sender.updateInventory();
            String x = Locale.GIVE_YOURSELF.getString()
                    .replace("%material%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            sender.sendMessage(x);
            String toSend = Locale.GIVE_SELF_STAFF.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%executor%", executor)
                    .replace("%material%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(
                    toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE
            ));
        } else {
            sender.sendMessage(Locale.INVALID_MATERIAL.getString());
        }
    }

    @Command(names = {"craft", "workbench"}, permission = Perms.CRAFT)
    public void craft(Player sender) {
        sender.openWorkbench(sender.getLocation(), true);
    }

    @Command(names = "clear", permission = Perms.CLEAR)
    public void clear(CommandSender sender, @Param(name = "player", baseValue = "self") Player target) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (target == player) {
                clearPlayer(player);
                player.sendMessage(Locale.CLEAR_SELF.getString());
            } else {
                String executor = Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(player.getUniqueId()));
                clearPlayer(target);
                target.sendMessage(Locale.CLEAR_TARGET.getString().replace("%player%", sender.getName()));
                player.sendMessage(Locale.CLEAR_SENDER.getString().replace("%player%", target.getName()));
                String toSend = Locale.CLEAR_PLAYER_STAFF.getString()
                        .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                        .replace("%executor%", executor)
                        .replace("%player%", target.getName());
                Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(
                        toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE
                ));
            }
        } else {
            clearPlayer(target);
            target.sendMessage(Locale.CLEAR_TARGET.getString().replace("%player%", "Console"));
        }

    }

    @Command(names = "heal", permission = Perms.HEAL)
    public void heal(CommandSender sender, @Param(name = "target", baseValue = "self") Player target) {
        target.setHealth(target.getMaxHealth());
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String executor = Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(player.getUniqueId()));
            if (target == player) {
                player.sendMessage(Locale.HEAL_SELF.getString());
                String toSend = Locale.HEAL_STAFF.getString()
                        .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                        .replace("%executor%", executor)
                        .replace("%player%", "himself");
                Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(
                        toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE
                ));
            } else {
                target.sendMessage(Locale.HEAL_TARGET.getString().replace("%player%", sender.getName()));
                player.sendMessage(Locale.HEAL_SENDER.getString().replace("%player%", target.getName()));
                String toSend = Locale.HEAL_STAFF.getString()
                        .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                        .replace("%executor%", executor)
                        .replace("%player%", target.getName());
                Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(
                        toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE
                ));
            }
        } else {
            target.sendMessage(Locale.HEAL_TARGET.getString().replace("%player%", "Console"));
        }
    }

    @Command(names = "feed", permission = Perms.FEED)
    public void feed(CommandSender sender, @Param(name = "target", baseValue = "self") Player target) {
        target.setSaturation(20);
        target.setFoodLevel(20);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (target == player) {
                player.sendMessage(Locale.FEED_SELF.getString());
            } else {
                target.sendMessage(Locale.FEED_TARGET.getString().replace("%player%", sender.getName()));
                player.sendMessage(Locale.FEED_SENDER.getString().replace("%player%", target.getName()));
            }
        } else {
            target.sendMessage(Locale.FEED_TARGET.getString().replace("%player%", "Console"));
        }
    }

    @Command(names = {"enchant"}, permission = Perms.ENCHANT)
    public void enchant(Player sender, @Param(name = "enchantment") String enchant, @Param(name = "level") int level) {
        ItemStack item = sender.getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            sender.sendMessage(Locale.NEED_ITEM_IN_HAND.getString());
            return;
        }

        if (level < 0) {
            sender.sendMessage(Locale.LEVEL_NOT_IN_BOUNDS.getString());
            return;
        }

        if (level > 10 && !sender.isOp()) {
            sender.sendMessage(Locale.LEVEL_NOT_IN_BOUNDS.getString());
            return;
        }


        String enchantment = StringUtils.getEnchantment(enchant);

        if (level == 0) {
            if (item.containsEnchantment(Enchantment.getByName(enchantment))) {
                item.removeEnchantment(Enchantment.getByName(enchantment));

                sender.sendMessage(Locale.ENCHANT_REMOVED.getString().replace("%enchant%", enchant.toUpperCase()));
            } else {
                sender.sendMessage(Locale.DOES_NOT_HAVE_ENCHANTMENT.getString().replace("%enchant%", enchant.toUpperCase()));
            }
        } else {
            item.addUnsafeEnchantment(Enchantment.getByName(enchantment), level);
            String string = Locale.ENCHANT_ADDED.getString()
                    .replace("%enchant%", enchant.toUpperCase())
                    .replace("%level%", String.valueOf(level));
            sender.sendMessage(string);
        }
    }

    @Command(names = {"demomode", "demo"}, permission = Perms.DEMOMODE)
    public void demo(CommandSender sender, @Param(name = "player", baseValue = "self") Player target) {
        if (!Holiday.getInstance().isProtocolEnabled()) {
            sender.sendMessage(CC.translate("&cYou need ProtocolLib in order to run this command"));
            return;
        }

        final PacketContainer packet = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        packet.getIntegers().write(0, 5);
        packet.getFloat().write(0, 0.0f);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, packet);
            sender.sendMessage(Locale.DEMO_SCREEN.getString().replace("%player%", target.getName()));
        } catch (Exception ignored) {
            sender.sendMessage(Locale.PACKET_ERROR.getString());
        }
    }


    private void clearPlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }


}
