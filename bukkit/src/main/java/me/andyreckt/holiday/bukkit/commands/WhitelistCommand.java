package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.MainCommand;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.SubCommand;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@MainCommand(names = {"whitelist", "wl"}, permission = Perms.WHITELIST, description = "Whitelist command.")
public class WhitelistCommand {


    @SubCommand(names = {"on", "enable"}, async = true, description = "Enable the whitelist.")
    public void onToggleOn(CommandSender sender) {
        Holiday.getInstance().getThisServer().setWhitelisted(true);
        Bukkit.broadcastMessage(Locale.GLOBAL_WHITELIST_ENABLED.getString());
        String toSend = Locale.STAFF_WHITELIST_ENABLED.getString()
                .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName());
        PacketHandler.send(
                new BroadcastPacket(toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
    }

    @SubCommand(names = {"off", "disable"}, async = true, description = "Disable the whitelist.")
    public void onToggleOff(CommandSender sender) {
        Holiday.getInstance().getThisServer().setWhitelisted(false);
        Bukkit.broadcastMessage(Locale.GLOBAL_WHITELIST_DISABLED.getString());
        String toSend = Locale.STAFF_WHITELIST_DISABLED.getString()
                .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName());
        PacketHandler.send(
                new BroadcastPacket(toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
    }


    @SubCommand(names = {"setrank", "rank"}, async = true, description = "Set the rank required to join the server.")
    public void onRank(CommandSender sender, @Param(name = "rank") IRank rank) {
        Holiday.getInstance().getThisServer().setWhitelistRank(rank);
        sender.sendMessage(CC.translate(Locale.PLAYER_WHITELIST_RANK.getString().replace("%rank%", rank.getDisplayName())));
        String toSend = Locale.STAFF_WHITELIST_RANK.getString()
                .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                .replace("%rank%", rank.getDisplayName());
        PacketHandler.send(
                new BroadcastPacket(toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
    }


    @SubCommand(names = {"add"}, async = true, description = "Add a player to the whitelist.")
    public void onAdd(CommandSender sender, @Param(name = "player") OfflinePlayer player) {
        if (Holiday.getInstance().getThisServer().getWhitelistedPlayers().contains(player.getUniqueId())) {
            sender.sendMessage(CC.translate(Locale.PLAYER_ALREADY_WHITELISTED.getString()));
            return;
        }

        Holiday.getInstance().getThisServer().getWhitelistedPlayers().add(player.getUniqueId());
        sender.sendMessage(CC.translate(Locale.PLAYER_WHITELIST_ADDED.getString().replace("%player%", UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(player.getUniqueId())))));
    }

    @SubCommand(names = {"remove"}, async = true, description = "Remove a player from the whitelist.")
    public void onremove(CommandSender sender, @Param(name = "player") OfflinePlayer player) {
        if (!Holiday.getInstance().getThisServer().getWhitelistedPlayers().contains(player.getUniqueId())) {
            sender.sendMessage(CC.translate(Locale.PLAYER_NOT_WHITELISTED.getString()));
            return;
        }

        Holiday.getInstance().getThisServer().getWhitelistedPlayers().remove(player.getUniqueId());
        sender.sendMessage(CC.translate(Locale.PLAYER_WHITELIST_REMOVED.getString().replace("%player%", UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(player.getUniqueId())))));
    }

    @SubCommand(names = {"status", "info"}, async = true, description = "Get information about the whitelist status.")
    public void onInfo(CommandSender sender) {
        sender.sendMessage(CC.CHAT_BAR);

        String status = Holiday.getInstance().getThisServer().isWhitelisted() ? "&aEnabled" : "&cDisabled";
        sender.sendMessage(CC.PRIMARY + "Whitelist Info");
        sender.sendMessage(CC.CHAT + " Whitelist status: " + CC.translate(status));
        sender.sendMessage(CC.CHAT + " Whitelist rank: " + CC.translate(Holiday.getInstance().getThisServer().getWhitelistRank().getDisplayName()));

        StringBuilder s = new StringBuilder();

        for (UUID uid : Holiday.getInstance().getThisServer().getWhitelistedPlayers()) {
            Profile pr = Holiday.getInstance().getApi().getProfile(uid);
            s.append(CC.CHAT).append(UserConstants.getNameWithColor(pr)).append(CC.CHAT).append(", ");
        }
        if (s.length() > 5)
            s = new StringBuilder(s.substring(0, s.length() - 4));
        s = new StringBuilder("&7[" + s + "&7]");
        if (s.toString().equalsIgnoreCase("&7[&7]")) {
            sender.sendMessage(CC.CHAT + " Whitelisted players: " + CC.PRIMARY + "None");
        } else {
            sender.sendMessage(CC.CHAT + " Whitelisted players: " + CC.translate(s.toString()));
        }

        sender.sendMessage(CC.CHAT_BAR);
    }

}
