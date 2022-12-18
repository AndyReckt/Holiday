package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportCommands {

    @Command(names = {"teleportall", "tpall"}, permission = Perms.TELEPORT_ALL, description = "Teleport all players to you.", usage = "/tpall")
    public void tpall(Player sender) {
        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(sender));
        sender.sendMessage(Locale.TELEPORT_PLAYER_ALL.getString());
        String message = Locale.TELEPORT_STAFF_PLAYER_ALL.getString()
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                .replace("%executor%", Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId())));
        Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(message, Perms.STAFF_VIEW_NOTIFICATIONS.get()));
    }

    @Command(names = {"teleport", "tp"}, permission = Perms.TELEPORT, description = "Teleport to a player.", usage = "/tp <player>")
    public void tp(Player sender, @Param(name = "player") Player target) {
        sender.teleport(target.getLocation());
        String message = Locale.TELEPORT_STAFF_PLAYER.getString()
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                .replace("%executor%", Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId())))
                .replace("%target%", Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(target.getUniqueId())));
        Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(message, Perms.STAFF_VIEW_NOTIFICATIONS.get()));
    }

    @Command(names = {"teleporthere", "tph", "tphere", "s"}, permission = Perms.TELEPORT_HERE, description = "Teleport a player to you.", usage = "/tph <player>")
    public void tphere(Player sender, @Param(name = "player") Player target) {
        target.teleport(sender.getLocation());
        String message = Locale.TELEPORT_STAFF_PLAYER_HERE.getString()
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                .replace("%executor%", Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId())))
                .replace("%target%", Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(target.getUniqueId())));
        Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(message, Perms.STAFF_VIEW_NOTIFICATIONS.get()));
    }

    @Command(names = {"teleportposition", "tpposition", "teleportpos", "tppos"}, permission = Perms.TELEPORT_POSITION, description = "Teleport to a position.", usage = "/tppos <x> <y> <z>")
    public void tpPos(Player sender, @Param(name = "x") double x, @Param(name = "y") double y, @Param(name = "z") double z) {
        if (x > 3000000 || y > 260 || z > 3000000 || x < -3000000 || y < -10 || z < -3000000) {
            sender.sendMessage(Locale.MAXIMUM_COORDINATE.getString());
            return;
        }
        Location location = new Location(sender.getWorld(), x, y, z, sender.getLocation().getYaw(), sender.getLocation().getPitch());
        sender.teleport(location);
        String message = Locale.TELEPORT_STAFF_PLAYER_POSITION.getString()
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                .replace("%executor%", Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId())))
                .replace("%x%", String.valueOf(x))
                .replace("%y%", String.valueOf(y))
                .replace("%z%", String.valueOf(z));
        Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(message, Perms.STAFF_VIEW_NOTIFICATIONS.get()));
    }

}
