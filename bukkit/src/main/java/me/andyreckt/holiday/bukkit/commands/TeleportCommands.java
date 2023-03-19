package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
 
  
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportCommands extends BaseCommand {

    @CommandPermission("core.command.teleportall")
    @CommandAlias("teleportall|tpall")
    public void tpall(Player sender) {
        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(sender));
        sender.sendMessage(Locale.TELEPORT_PLAYER_ALL.getString());
        String message = Locale.TELEPORT_STAFF_PLAYER_ALL.getString()
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                .replace("%executor%", UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId())));
        PacketHandler.send(
                new BroadcastPacket(message, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
    }

    @CommandPermission("core.command.teleport")
    @CommandAlias("teleport|tp")
    @CommandCompletion("@players")
    public void tp(Player sender, @Single @Name("player") Player target) {
        sender.teleport(target.getLocation());
        String message = Locale.TELEPORT_STAFF_PLAYER.getString()
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                .replace("%executor%", UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId())))
                .replace("%player%", UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(target.getUniqueId())));
        PacketHandler.send(
                new BroadcastPacket(message, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
    }

    @CommandCompletion("@players")
    @CommandAlias("teleporthere|tphere|tph|s")
    @CommandPermission("core.command.teleporthere")
    public void tphere(Player sender, @Single @Name("player") Player target) {
        target.teleport(sender.getLocation());
        String message = Locale.TELEPORT_STAFF_PLAYER_HERE.getString()
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                .replace("%executor%", UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId())))
                .replace("%player%", UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(target.getUniqueId())));
        PacketHandler.send(new BroadcastPacket(message, Perms.STAFF_VIEW_NOTIFICATIONS.get()));
    }

    @CommandAlias("teleportposition|tpposition|teleportpos|tppos")
    @CommandPermission("core.command.teleportposition")
    public void tpPos(Player sender, @Single @Name("x") double x, @Single @Name("y") double y, @Single @Name("z") double z) {
        if (x > 3000000 || y > 260 || z > 3000000 || x < -3000000 || y < -10 || z < -3000000) {
            sender.sendMessage(Locale.MAXIMUM_COORDINATE.getString());
            return;
        }
        Location location = new Location(sender.getWorld(), x, y, z, sender.getLocation().getYaw(), sender.getLocation().getPitch());
        sender.teleport(location);
        String message = Locale.TELEPORT_STAFF_PLAYER_POSITION.getString()
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                .replace("%executor%", UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId())))
                .replace("%x%", String.valueOf(x))
                .replace("%y%", String.valueOf(y))
                .replace("%z%", String.valueOf(z));
        PacketHandler.send(
                new BroadcastPacket(message, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
    }

}
