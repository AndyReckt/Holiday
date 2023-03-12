package me.andyreckt.holiday.bukkit.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.andyreckt.holiday.api.server.IServer;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.menu.punishments.check.PunishmentCheckMenu;
import me.andyreckt.holiday.bukkit.server.menu.punishments.list.PunishmentListMenu;
import me.andyreckt.holiday.bukkit.server.menu.staff.InvSeeMenu;
import me.andyreckt.holiday.bukkit.server.redis.packet.CrossServerCommandPacket;
import me.andyreckt.holiday.bukkit.server.redis.packet.HelpopPacket;
import me.andyreckt.holiday.bukkit.server.redis.packet.ReportPacket;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.other.Cooldown;
import me.andyreckt.holiday.bukkit.util.player.PlayerList;
import me.andyreckt.holiday.bukkit.util.player.PlayerUtils;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.util.duration.Duration;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.enums.ChatChannel;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EssentialCommands {

    private final Map<UUID, Cooldown> reportCooldownMap = new HashMap<>();
    private final Map<UUID, Cooldown> helpopCooldownMap = new HashMap<>();

    @Command(names = {"report"}, async = true)
    public void report(Player sender, @Param(name = "target") Player target, @Param(name = "reason", wildcard = true) String reason) {

        if (sender == target) {
            sender.sendMessage(Locale.CANNOT_REPORT_YOURSELF.getString());
            return;
        }

        if (reportCooldownMap.containsKey(sender.getUniqueId())) {
            Cooldown oldCd = reportCooldownMap.get(sender.getUniqueId());
            if (oldCd.hasExpired()) reportCooldownMap.remove(sender.getUniqueId());
            else {
                sender.sendMessage(Locale.COOLDOWN.getString().replace("%time%", Duration.of(oldCd.getRemaining()).toSmallRoundedTime()));
                return;
            }
        }

        Cooldown cd = Cooldown.fromSeconds(Locale.REPORT_COOLDOWN.getInt());
        reportCooldownMap.put(sender.getUniqueId(), cd);

        sender.sendMessage(Locale.REPORT_MESSAGE.getString());
        Profile profile = Holiday.getInstance().getApi().getProfile(sender.getUniqueId());
        Profile targetProfile = Holiday.getInstance().getApi().getProfile(target.getUniqueId());

        ReportPacket packet = new ReportPacket(
                UserConstants.getDisplayNameWithColor(profile),
                UserConstants.getDisplayNameWithColor(targetProfile),
                reason,
                Holiday.getInstance().getThisServer().getServerName()
        );

        PacketHandler.send(packet);
    }

    @Command(names = {"request", "helpop", "helpme", "question", "ask",}, async = true)
    public void request(Player sender, @Param(name = "reason", wildcard = true) String reason) {

        if (helpopCooldownMap.containsKey(sender.getUniqueId())) {
            Cooldown oldCd = helpopCooldownMap.get(sender.getUniqueId());
            if (oldCd.hasExpired()) helpopCooldownMap.remove(sender.getUniqueId());
            else {
                sender.sendMessage(Locale.COOLDOWN.getString().replace("%time%", Duration.of(oldCd.getRemaining()).toSmallRoundedTime()));
                return;
            }
        }

        Cooldown cd = Cooldown.fromSeconds(Locale.HELPOP_COOLDOWN.getInt());
        helpopCooldownMap.put(sender.getUniqueId(), cd);

        sender.sendMessage(Locale.HELPOP_MESSAGE.getString());
        PacketHandler.send(new HelpopPacket(
                UserConstants.getDisplayNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId())),
                reason,
                Holiday.getInstance().getThisServer().getServerName()
        ));
    }

    @Command(names = {"ping", "ms", "latency"})
    public void ping(Player sender, @Param(name = "target", baseValue = "self") Player target) {
        if (target != sender) {
            String diff = String.valueOf(Math.max(PlayerUtils.getPing(sender), PlayerUtils.getPing(target)) - Math.min(PlayerUtils.getPing(sender), PlayerUtils.getPing(target)));
            String str = Locale.PING_OTHER.getString()
                    .replace("%player%", target.getName())
                    .replace("%ping%", String.valueOf(PlayerUtils.getPing(target)))
                    .replace("%difference%", diff);

            sender.sendMessage(str);
        } else {
            sender.sendMessage(Locale.PING.getString().replace("%ping%", String.valueOf(PlayerUtils.getPing(sender))));
        }
    }

    @Command(names = {"who", "list"}, async = true)
    public void list(CommandSender sender) {
        StringBuilder builder = new StringBuilder();

        IRank[] ranks = Holiday.getInstance().getApi().getRanksSorted().toArray(new IRank[]{});

        Arrays.stream(ranks).filter(IRank::isVisible).forEach(rank
                -> builder.append(CC.translate(rank.getDisplayName())).append(CC.GRAY).append(", "));

        builder.setCharAt(builder.length() - 2, '.');

        builder.append("\n");

        List<String> players = PlayerList.getVisiblyOnline(sender)
                .visibleRankSorted().asColoredNames();

        builder.append(CC.GRAY).append("(").append(PlayerList.getVisiblyOnline(sender).getPlayers().size()).append("/")
                .append(Holiday.getInstance().getServer().getMaxPlayers()).append("): ")
                .append("&7[")
                .append(players.toString().replace("[", "").replace("]", ""))
                .append("&7]");

        sender.sendMessage(CC.translate(builder.toString()));
    }

    @Command(names = "rename", permission = Perms.RENAME)
    public void rename(Player sender, @Param(name = "name", wildcard = true) String name) {
        ItemStack is = sender.getItemInHand();
        if (is == null || is.getType().equals(Material.AIR)) {
            sender.sendMessage(CC.translate("&cYou must hold an item in order to rename it."));
            return;
        }
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(CC.translate(name));

        String itemName = StringUtils.capitalize(is.getType().name().toLowerCase().replace("_", " "));
        sender.getItemInHand().setItemMeta(im);

        sender.updateInventory();
        sender.sendMessage(CC.translate(
                Locale.RENAME.getString()
                        .replace("%item%", itemName)
                        .replace("%name%", name)
        ));
    }

    @Command(names = {"garbage"}, permission = Perms.GARBAGE, async = true)
    public void gc(CommandSender sender) {
        System.gc();
        sender.sendMessage(CC.translate("&aSuccessfully ran the garbage collector."));
    }

    @Command(names = "fly", permission = Perms.FLY)
    public void fly(Player sender) {
        sender.setAllowFlight(!sender.getAllowFlight());
        String executor = UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId()));
        if (sender.getAllowFlight()) {
            sender.sendMessage(Locale.FLY_ENABLED.getString());
            String toSend = Locale.FLY_ENABLED_STAFF.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%executor%", executor);
            PacketHandler.send(new BroadcastPacket(
                    toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE
            ));
        } else {
            String toSend = Locale.FLY_DISABLED_STAFF.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%executor%", executor);
            PacketHandler.send(new BroadcastPacket(
                    toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE
            ));
            sender.sendMessage(Locale.FLY_DISABLED.getString());
        }
    }

    @Command(names = {"give"}, permission = Perms.GIVE)
    public void give(Player sender, @Param(name = "player") Player target, @Param(name = "material") String material, @Param(name = "amount") int amount) {
        Material mat = Bukkit.getUnsafe().getMaterialFromInternalName(material);
        if (mat != null) {
            String executor = UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId()));
            ItemStack Item = new ItemStack(mat, amount);
            PlayerInventory inv = target.getInventory();
            inv.addItem(Item);
            target.updateInventory();
            String x = Locale.GIVE_SENDER.getString()
                    .replace("%player%", target.getName())
                    .replace("%item%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            sender.sendMessage(CC.translate(x));
            String y = Locale.GIVE_TARGET.getString()
                    .replace("%player%", sender.getName())
                    .replace("%item%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            target.sendMessage(CC.translate(y));

            String toSend = Locale.GIVE_STAFF.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%executor%", executor)
                    .replace("%player%", target.getName())
                    .replace("%item%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            PacketHandler.send(new BroadcastPacket(
                    toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE
            ));
        } else {
            sender.sendMessage(Locale.INVALID_MATERIAL.getString());
        }
    }

    @Command(names = {"giveall"}, permission = Perms.GIVEALL)
    public void giveall(Player sender, @Param(name = "material") String material, @Param(name = "amount") int amount) {
        Material mat = Bukkit.getUnsafe().getMaterialFromInternalName(material);
        String executor = UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId()));
        if (mat != null) {
            ItemStack Item = new ItemStack(mat, amount);
            String x = Locale.GIVE_ALL.getString()
                    .replace("%item%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            String y = Locale.GIVE_ALL_BROADCAST.getString()
                    .replace("%player%", sender.getName())
                    .replace("%item%", mat.name())
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
                    .replace("%item%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            PacketHandler.send(new BroadcastPacket(
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
            String executor = UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId()));
            ItemStack Item = new ItemStack(mat, amount);
            PlayerInventory inv = sender.getInventory();
            inv.addItem(Item);
            sender.updateInventory();
            String x = Locale.GIVE_YOURSELF.getString()
                    .replace("%item%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            sender.sendMessage(x);
            String toSend = Locale.GIVE_SELF_STAFF.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%executor%", executor)
                    .replace("%item%", mat.name())
                    .replace("%amount%", String.valueOf(amount));
            PacketHandler.send(new BroadcastPacket(
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
                String executor = UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(player.getUniqueId()));
                clearPlayer(target);
                target.sendMessage(Locale.CLEAR_TARGET.getString().replace("%player%", sender.getName()));
                player.sendMessage(Locale.CLEAR_SENDER.getString().replace("%player%", target.getName()));
                String toSend = Locale.CLEAR_PLAYER_STAFF.getString()
                        .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                        .replace("%executor%", executor)
                        .replace("%player%", target.getName());
                PacketHandler.send(new BroadcastPacket(
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
            String executor = UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(player.getUniqueId()));
            if (target == player) {
                player.sendMessage(Locale.HEAL_SELF.getString());
                String toSend = Locale.HEAL_STAFF.getString()
                        .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                        .replace("%executor%", executor)
                        .replace("%player%", "himself");
                PacketHandler.send(new BroadcastPacket(
                        toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE
                ));
            } else {
                target.sendMessage(Locale.HEAL_TARGET.getString().replace("%player%", sender.getName()));
                player.sendMessage(Locale.HEAL_SENDER.getString().replace("%player%", target.getName()));
                String toSend = Locale.HEAL_STAFF.getString()
                        .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                        .replace("%executor%", executor)
                        .replace("%player%", target.getName());
                PacketHandler.send(new BroadcastPacket(
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


        String enchantment = me.andyreckt.holiday.bukkit.util.text.StringUtils.getEnchantment(enchant);

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

    @Command(names = "killall", permission = Perms.KILLALL)
    public void killall(CommandSender sender, @Param(name = "<all|mobs|animals|items>", baseValue = "all", tabCompleteFlags = {"all", "mobs", "animals", "items"}) String arg) {
        int total = 0;
        switch (arg) {
            case "mob":
            case "mobs": {
                total += killAll(Monster.class);
                break;
            }
            case "animals":
            case "animal": {
                total += killAll(Animals.class);
                break;
            }
            case "items":
            case "item": {
                total += killAll(Item.class);
                break;
            }
            case "all":
            default: {
                total += killAll(Monster.class);
                total += killAll(Animals.class);
                total += killAll(Item.class);
                break;
            }
        }
        sender.sendMessage(Locale.KILL_ALL.getString().replace("%total%", String.valueOf(total)));
    }

    @Command(names = {"setmaxplayer", "setslots", "slots"}, permission = Perms.SET_MAX_PLAYERS)
    public void slots(CommandSender sender, @Param(name = "player") int players) {
        me.andyreckt.holiday.bukkit.util.text.StringUtils.setSlots(players);
        Holiday.getInstance().getThisServer().setMaxPlayers(players);
        sender.sendMessage(Locale.MAX_PLAYERS.getString().replace("%amount%", String.valueOf(players)));
    }

    @Command(names = {"more"}, permission = Perms.MORE)
    public void more(Player sender) {
        ItemStack item = sender.getItemInHand();
        if (item == null || item.getType() == Material.AIR) {
            sender.sendMessage(Locale.NEED_ITEM_IN_HAND.getString());
            return;
        }
        if (item.getAmount() >= 64) {
            sender.sendMessage(Locale.ITEM_ALREADY_STACKED.getString());
            return;
        }
        item.setAmount(64);
        sender.updateInventory();
        sender.sendMessage(Locale.ITEM_STACKED.getString());
    }

    @Command(names = "sudo", permission = Perms.SUDO)
    public void sudo(CommandSender sender, @Param(name = "target") Player target, @Param(name = "message", wildcard = true) String msg) {
        String string = Locale.SUDO_PLAYER.getString()
                .replace("%player%", target.getName())
                .replace("%text%", msg);
        sender.sendMessage(string);
        target.chat(msg);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String senderName = UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(player.getUniqueId()));
            String toSend = Locale.SUDO_STAFF.getString()
                    .replace("%executor%", senderName)
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%player%", target.getName())
                    .replace("%text%", msg);
            PacketHandler.send(new BroadcastPacket(toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
        }
    }

    @Command(names = {"sudoall", "massay"}, permission = Perms.SUDOALL)
    public void suadoall(CommandSender sender, @Param(name = "message", wildcard = true) String msg) {
        Bukkit.getOnlinePlayers().stream()
                .map(Player::getUniqueId)
                .map(Holiday.getInstance().getApi()::getProfile)
                .filter(profile -> !profile.isMuted())
                .map(Profile::getUuid)
                .map(Bukkit::getPlayer)
                .forEach(player -> player.chat(msg));
        sender.sendMessage(Locale.SUDO_ALL_PLAYER.getString().replace("%text%", msg));
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String senderName = UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(player.getUniqueId()));
            String toSend = Locale.SUDO_ALL_STAFF.getString()
                    .replace("%executor%", senderName)
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%text%", msg);
            PacketHandler.send(new BroadcastPacket(toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
        }
    }

    @Command(names = {"invsee", "inv"}, permission = Perms.INVSEE)
    public void invsee(Player player, @Param(name = "player") Player target) {
        new InvSeeMenu(target).openMenu(player);
    }

    @Command(names = {"check", "c", "checkban", "checkpun", "checkpunishments", "punishments", "bancheck", "mutecheck", "punishmentcheck", "punishcheck", "pcheck"}, permission = Perms.CHECK_PUNISHMENTS)
    public void check(Player player, @Param(name = "player") Profile target) {
        new PunishmentCheckMenu(target).openMenu(player);
    }

    @Command(names = {"punishmentlist", "plist", "banlist", "mutelist", "blacklistlist"}, permission = Perms.PUNISHMENT_LIST)
    public void punishmentsList(Player player) {
        new PunishmentListMenu().openMenu(player);
    }

    @Command(names = {"alts", "alt", "accounts", "associatedaccounts", "listallaccounts"}, permission = Perms.ALTS, async = true)
    public void alts(CommandSender sender, @Param(name = "player") Profile target) {
        StringBuilder alts = new StringBuilder();
        alts.append("&7[");
        int i = 0;
        for (String alt : target.getAltsFormatted()) {
            i++;
            if (i == target.getAlts().size()) {
                alts.append(alt);
            } else {
                alts.append(alt).append("&7, ");
            }
        }
        alts.append("&7] (").append(i).append(i == 1 ? " account" : " accounts").append(")");

        sender.sendMessage(CC.translate("&7[&aOnline&7, &7Offline&7, &eMuted&7, &cBanned&7, &4Blacklisted&7]"));
        sender.sendMessage(CC.translate("&7&oThe accounts associated to this profile are: "));
        sender.sendMessage(CC.translate(alts.toString()));
    }

    @Command(names = {"sc", "staffchat", "staffc"}, permission = Perms.STAFF_CHAT, async = true)
    public void staffchat(Player player, @Param(name = "message", wildcard = true, baseValue = "$toggle$") String message) {
        UserProfile profile = (UserProfile) Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        if (message.equalsIgnoreCase("$toggle$")) {
            boolean bool = profile.getChatChannel() == ChatChannel.STAFF;
            Locale locale = (!bool) ? Locale.CHAT_CHANNEL_JOIN : Locale.CHAT_CHANNEL_LEAVE;
            player.sendMessage(locale.getString().replace("%channel%", ChatChannel.STAFF.getName()));
            profile.setChatChannel(bool ? ChatChannel.GLOBAL : ChatChannel.STAFF);
            Holiday.getInstance().getApi().saveProfile(profile);
            return;
        }

        String playerName = UserConstants.getNameWithColor(profile);
        String server = Holiday.getInstance().getThisServer().getServerName();
        String toSend = Locale.STAFF_CHAT.getString()
                .replace("%player%", playerName)
                .replace("%server%", server)
                .replace("%message%", message);
        PacketHandler.send(new BroadcastPacket(
                toSend,
                Perms.STAFF_CHAT.get(),
                AlertType.STAFF_CHAT
        ));
    }

    @Command(names = {"adminchat", "achat", "ac"}, permission = Perms.ADMIN_CHAT, async = true)
    public void adminchat(Player player, @Param(name = "message", wildcard = true, baseValue = "$toggle$") String message) {
        UserProfile profile = (UserProfile) Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        if (message.equalsIgnoreCase("$toggle$")) {
            boolean bool = profile.getChatChannel() == ChatChannel.ADMIN;
            Locale locale = (!bool) ? Locale.CHAT_CHANNEL_JOIN : Locale.CHAT_CHANNEL_LEAVE;
            player.sendMessage(locale.getString().replace("%channel%", ChatChannel.ADMIN.getName()));
            profile.setChatChannel(bool ? ChatChannel.GLOBAL : ChatChannel.ADMIN);
            Holiday.getInstance().getApi().saveProfile(profile);
            return;
        }

        String playerName = UserConstants.getNameWithColor(profile);
        String server = Holiday.getInstance().getThisServer().getServerName();
        String toSend = Locale.ADMIN_CHAT.getString()
                .replace("%player%", playerName)
                .replace("%server%", server)
                .replace("%message%", message);
        PacketHandler.send(new BroadcastPacket(
                toSend,
                Perms.ADMIN_CHAT.get(),
                AlertType.ADMIN_CHAT
        ));
    }

    @Command(names = "join", permission = Perms.JOIN)
    public void join(Player sender, @Param(name = "server") String server) {
        IServer data = Holiday.getInstance().getApi().getServer(server);

        if (data == null || !data.isOnline()) {
            sender.sendMessage(Locale.SERVER_NOT_FOUND.getString());

            StringBuilder sb = new StringBuilder();
            sb.append("&cAvailable servers: ");
            for (Map.Entry<String, IServer> entry : Holiday.getInstance().getApi().getServers().entrySet()) {
                if (entry.getValue().isOnline()) sb.append("&c").append(entry.getKey()).append("&7,");
            }
            sender.sendMessage(CC.translate(sb.substring(0, sb.length() - 3)));
            return;
        }
        sender.sendMessage(Locale.JOINING_SERVER.getString().replace("%server%", data.getServerName()));
        PlayerUtils.sendToServer(sender, server);
    }


    @Command(names = "pull", permission = Perms.PULL)
    public void pull(Player sender, @Param(name = "player") Profile player) {

        if (!player.isOnline()) {
            sender.sendMessage(Locale.PLAYER_NOT_ONLINE.getString());
            return;
        }

        if (player.getCurrentServer() == null) {
            sender.sendMessage(Locale.PLAYER_NOT_ONLINE.getString());
            return;
        }

        sender.sendMessage(Locale.PULLING_PLAYER.getString().replace("%player%", UserConstants.getDisplayNameWithColor(player)));
        PacketHandler.send(new CrossServerCommandPacket(
                "sendtoserver " + player.getDisplayName() + " " + Holiday.getInstance().getThisServer().getServerId(), player.getCurrentServer().getServerId()));
    }

    @Command(names = "sendtoserver", permission = Perms.SEND_TO_SERVER)
    public void send(CommandSender sender, @Param(name = "player") Player player, @Param(name = "server") String server) {
        IServer data = Holiday.getInstance().getApi().getServer(server);

        if (data == null || !data.isOnline()) {
            sender.sendMessage(Locale.SERVER_NOT_FOUND.getString());

            StringBuilder sb = new StringBuilder();
            sb.append("&cAvailable servers: ");
            for (Map.Entry<String, IServer> entry : Holiday.getInstance().getApi().getServers().entrySet()) {
                if (entry.getValue().isOnline()) sb.append("&c").append(entry.getKey()).append("&7,");
            }
            sender.sendMessage(CC.translate(sb.substring(0, sb.length() - 3)));
            return;
        }
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
        sender.sendMessage(Locale.SENDING_PLAYER.getString()
                .replace("%player%", UserConstants.getDisplayNameWithColor(profile))
                .replace("%server%", data.getServerName()));
        PlayerUtils.sendToServer(player, server);
    }

    @Command(names = {"find", "search"}, permission = Perms.FIND)
    public void find(CommandSender sender, @Param(name = "player") Profile player) {
        if (player.isOnline()) {
            sender.sendMessage(Locale.PLAYER_CONNECTED_TO.getString()
                    .replace("%player%", UserConstants.getDisplayNameWithColor(player))
                    .replace("%server%", player.getCurrentServer().getServerName()));
        } else {
            sender.sendMessage(Locale.PLAYER_NOT_ONLINE.getString());
        }
    }

    @Command(names = {"lag", "serverlag"}, permission = Perms.LAG)
    public void lag(CommandSender sender) {
        StringBuilder sb = new StringBuilder(" ");
        for (double tps : Holiday.getInstance().getNms().recentTps()) {
            sb.append(format(tps));
            sb.append(", ");
        }

        IServer server = Holiday.getInstance().getThisServer();

        String uptime = DurationFormatUtils.formatDurationWords(server.getUptime(), true, true);
        String tps = sb.substring(0, sb.length() - 2);

        Locale.LAG_MESSAGE.getStringList().forEach(s -> {
            if (s.equalsIgnoreCase("%worlds%")) {
                for (World world : Bukkit.getWorlds()) {
                    sender.sendMessage(CC.translate(
                            Locale.LAG_WORLDS.getString()
                                    .replace("%name%", world.getName())
                                    .replace("%chunks%", String.valueOf(world.getLoadedChunks().length))
                                    .replace("%entities%", String.valueOf(world.getEntities().size())))
                    );
                }
            } else
                sender.sendMessage(CC.translate(s
                        .replace("%bar%", CC.CHAT_BAR)
                        .replace("%tps%", tps)
                        .replace("%uptime%", uptime)
                        .replace("%mem_max%", String.valueOf(Runtime.getRuntime().maxMemory() / 1024 / 1024))
                        .replace("%mem_allocated%", String.valueOf(Runtime.getRuntime().totalMemory() / 1024 / 1024))
                        .replace("%mem_available%", String.valueOf(Runtime.getRuntime().freeMemory() / 1024 / 1024))
                ));
        });
    }

    @Command(names = "reloadconfig", permission = Perms.RELOAD)
    public void reloadConfig(CommandSender sender) {
        Holiday.getInstance().setupConfigFiles();
        sender.sendMessage(Locale.RELOAD_CONFIG.getString());
    }




    private int killAll(Class<? extends Entity> clazz) {
        int total = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntitiesByClass(clazz)) {
                entity.remove();
                total++;
            }
        }
        return total;
    }

    private String format(double tps) {
        return ((tps > 18.0) ? CC.GREEN : (tps > 16.0) ? CC.YELLOW : CC.RED)
                + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }


    private void clearPlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }


}
