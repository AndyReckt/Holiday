package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.PermissionUpdatePacket;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.MainCommand;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.SubCommand;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import org.bukkit.command.CommandSender;

@MainCommand(names = "user", permission = Perms.USER, description = "User command.")
public class UserCommand {

    @SubCommand(names = {"addpermission", "addperm"}, description = "Add permission to user.")
    public void addPermission(CommandSender sender, @Param(name = "player") Profile profile, @Param(name = "permission") String permission) {
        if (profile.getPermissions().contains(permission)) {
            sender.sendMessage(Locale.PLAYER_ALREADY_HAS_PERMISSION.getString());
            return;
        }
        profile.addPermission(permission);
        sender.sendMessage("Added permission " + permission + " to " + profile.getName() + ".");
        Holiday.getInstance().getApi().saveProfile(profile);
        PacketHandler.send(new PermissionUpdatePacket(profile.getUuid()));
    }

    @SubCommand(names = {"removepermission", "removeperm", "remperm"}, description = "Remove permission from user.")
    public void removePermission(CommandSender sender, @Param(name = "player") Profile profile, @Param(name = "permission") String permission) {
        if (!profile.getPermissions().contains(permission)) {
            sender.sendMessage(Locale.PLAYER_DOES_NOT_HAVE_PERMISSION.getString());
            return;
        }

        profile.removePermission(permission);
        sender.sendMessage("Removed permission " + permission + " from " + profile.getName() + ".");
        Holiday.getInstance().getApi().saveProfile(profile);
        PacketHandler.send(new PermissionUpdatePacket(profile.getUuid()));
    }



}
