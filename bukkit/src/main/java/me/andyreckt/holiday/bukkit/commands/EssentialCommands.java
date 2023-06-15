package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
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

public class EssentialCommands extends BaseCommand {

    private final Map<UUID, Cooldown> reportCooldownMap = new HashMap<>();
    private final Map<UUID, Cooldown> helpopCooldownMap = new HashMap<>();

    @CommandAlias("report")
    @CommandCompletion("@players @nothing")
    @Conditions("player")
    public void report(CommandSender sen, @Name("target") Player target, @Name("reason") String reason) {
        Player sender = (Player) sen;

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
                Holiday.getInstance().getThisServer().getServerName(),
                Holiday.getInstance().getThisServer().getServerId()
        );

        PacketHandler.send(packet);
    }

    @CommandAlias("request|helpop|helpme|question|ask")
    @Conditions("player")
    public void request(CommandSender sen, @Name("request") String reason) {
        Player sender = (Player) sen;
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

    @CommandAlias("ping|ms|latency")
    @CommandCompletion("@players")
    @Conditions("player")
    public void ping(CommandSender sen, @Name("target") @Default("self") Player target) {
        Player sender = (Player) sen;
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


    @CommandAlias("who|list")
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

    @CommandAlias("rename")
    @CommandPermission("core.command.rename")
    @Conditions("player")
    public void rename(CommandSender sen, @Name("name") String name) {
        Player sender = (Player) sen;
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


    @CommandAlias("garbage|gc")
    @CommandPermission("core.command.garbage")
    public void gc(CommandSender sender) {
        System.gc();
        sender.sendMessage(CC.translate("&aSuccessfully ran the garbage collector."));
    }

    @CommandAlias("fly|flight")
    @CommandPermission("core.command.fly")
    @Conditions("player")
    public void fly(CommandSender sen) {
        Player sender = (Player) sen;
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

    @CommandAlias("give")
    @CommandPermission("core.command.give")
    @CommandCompletion("@players @materials")
    @Conditions("player")
    public void give(CommandSender sen, @Single @Name("target") Player target, @Single @Name("material") String material, @Single @Name("amount") @Default("1") int amount) {
        Player sender = (Player) sen;
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

    @CommandCompletion("@materials")
    @CommandAlias("giveall")
    @CommandPermission("core.command.giveall")
    @Conditions("player")
    public void giveall(CommandSender sen, @Single @Name("material") String material, @Single @Name("amount") @Default("1") int amount) {
        Player sender = (Player) sen;
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


    @CommandAlias("giveme|gimme|i")
    @CommandPermission("core.command.give")
    @CommandCompletion("@materials")
    @Conditions("player")
    public void giveme(CommandSender sen, @Single @Name("material") String material, @Single @Name("amount") @Default("1") int amount) {
        Player sender = (Player) sen;
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


    @CommandAlias("craft|workbench")
    @CommandPermission("core.command.craft")
    @Conditions("player")
    public void craft(CommandSender sen) {
        Player sender = (Player) sen;
        sender.openWorkbench(sender.getLocation(), true);
    }

    @CommandCompletion("@players")
    @CommandAlias("clear|clearinventory|ci")
    @CommandPermission("core.command.clear")
    public void clear(CommandSender sender, @Name("target") @Default("self") Player target) {
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

    @CommandCompletion("@players")
    @CommandAlias("heal")
    @CommandPermission("core.command.heal")
    @Conditions("player")
    public void heal(CommandSender sender, @Name("target") @Default("self") Player target) {
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

    @CommandCompletion("@players")
    @CommandAlias("feed")
    @CommandPermission("core.command.feed")
    public void feed(CommandSender sender, @Name("target") @Default("self") Player target) {
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

    @CommandAlias("enchant")
    @CommandPermission("core.command.enchant")
    @CommandCompletion("@enchantments")
    @Conditions("player")
    public void enchant(CommandSender sen, @Name("enchantement") String enchant, @Single @Name("level") @Default("1") int level) {
        Player sender = (Player) sen;
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

    @CommandAlias("demomode|demo")
    @CommandCompletion("@players")
    @CommandPermission("core.command.demomode")
    public void demo(CommandSender sender, @Name("target") @Default("self") Player target) {
        try {
            Holiday.getInstance().getNms().sendDemoScreen(target);
            sender.sendMessage(Locale.DEMO_SCREEN.getString().replace("%player%", target.getName()));
        } catch (Exception ignored) {
            sender.sendMessage(Locale.PACKET_ERROR.getString());
        }
    }

    @CommandAlias("killall")
    @CommandCompletion("all|mobs|animals|items")
    @CommandPermission("core.command.killall")
    public void killall(CommandSender sender, @Single @Name("all|mobs|animals|items") String arg) {
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


    @CommandAlias("setmaxplayer|setslots|slots")
    @CommandPermission("core.command.slots")
    public void slots(CommandSender sender, @Single @Name("slots") int players) {
        me.andyreckt.holiday.bukkit.util.text.StringUtils.setSlots(players);
        Holiday.getInstance().getThisServer().setMaxPlayers(players);
        sender.sendMessage(Locale.MAX_PLAYERS.getString().replace("%amount%", String.valueOf(players)));
    }

    @CommandPermission("core.command.more")
    @CommandAlias("more")
    @Conditions("player")
    public void more(CommandSender sen) {
        Player sender = (Player) sen;
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

    @CommandCompletion("@players")
    @CommandAlias("sudo")
    @CommandPermission("core.command.sudo")
    public void sudo(CommandSender sender, @Name("target") Player target, @Name("message") String msg) {
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


    @CommandPermission("core.command.sudoall")
    @CommandAlias("sudoall|massay")
    public void suadoall(CommandSender sender, @Name("message") String msg) {
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

    @CommandAlias("invsee|inv")
    @CommandPermission("core.command.invsee")
    @Conditions("player")
    public void invsee(CommandSender player, @Name("target") Player target) {
        new InvSeeMenu(target).openMenu((Player) player);
    }

    @CommandPermission("core.command.checkpunishments")
    @CommandCompletion("@players")
    @CommandAlias("checkpunishments|checkpun|punishmentcheck|punishcheck|punishments|c")
    @Conditions("player")
    public void check(CommandSender player, @Name("player") Profile target) {
        new PunishmentCheckMenu(target).openMenu((Player) player);
    }

    @CommandAlias("punishmentlist|plist|banlist|mutelist|blacklistlist")
    @CommandPermission("core.command.punishmentlist")
    @Conditions("player")
    public void punishmentsList(CommandSender player) {
        new PunishmentListMenu().openMenu((Player) player);
    }

    @CommandAlias("alts|alt|accounts|associatedaccounts|listallaccounts")
    @CommandPermission("core.command.alts")
    @CommandCompletion("@players")
    public void alts(CommandSender sender, @Name("player") Profile target) {
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

    @CommandAlias("staffchat|sc|staffc")
    @CommandPermission("core.staff.chat")
    @Conditions("player")
    public void staffchat(CommandSender sender, @Name("message") @Default("$toggle$") String message) {
        Player player = (Player) sender;
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

    @CommandAlias("adminchat|achat|ac")
    @CommandPermission("core.admin.chat")
    @Conditions("player")
    public void adminchat(CommandSender sender, @Name("message") @Default("$toggle$") String message) {
        Player player = (Player) sender;
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

    @CommandAlias("join|j")
    @CommandCompletion("@servers")
    @CommandPermission("core.command.join")
    @Conditions("player")
    public void join(CommandSender sender, @Name("server") String server) {
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
        PlayerUtils.sendToServer((Player) sender, server);
    }


    @CommandAlias("pull|p")
    @CommandPermission("core.command.pull")
    @Conditions("player")
    public void pull(CommandSender sender, @Name("player") Profile player) {

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

    @CommandAlias("sendtoserver")
    @CommandPermission("core.command.sendtoserver")
    @CommandCompletion("@players @servers")
    public void send(CommandSender sender, @Name("player") Player player, @Single @Name("server") String server) {
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

    @CommandCompletion("@players")
    @CommandAlias("find|search")
    @CommandPermission("core.command.find")
    public void find(CommandSender sender, @Name("player") Profile player) {
        if (player.isOnline()) {
            sender.sendMessage(Locale.PLAYER_CONNECTED_TO.getString()
                    .replace("%player%", UserConstants.getDisplayNameWithColor(player))
                    .replace("%server%", player.getCurrentServer().getServerName()));
        } else {
            sender.sendMessage(Locale.PLAYER_NOT_ONLINE.getString());
        }
    }

    @CommandAlias("lag|serverlag")
    @CommandPermission("core.command.lag")
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

    @CommandPermission("core.command.reload")
    @CommandAlias("reloadconfig|rlconfig")
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
