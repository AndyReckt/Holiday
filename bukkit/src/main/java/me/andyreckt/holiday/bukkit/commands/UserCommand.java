package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.KickPlayerPacket;
import me.andyreckt.holiday.bukkit.server.redis.packet.PermissionUpdatePacket;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.MainCommand;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.SubCommand;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.http.UUIDFetcher;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@MainCommand(names = "user", permission = Perms.USER, description = "User command.")
public class UserCommand {

    @SubCommand(names = {"addpermission", "addperm"}, description = "Add permission to an user.")
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

    @SubCommand(names = {"removepermission", "removeperm", "remperm"}, description = "Remove permission from me.andyreckt.holiday.user.")
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

    @SubCommand(names = {"wipe", "delete"}, description = "Wipes a profile.", permission = Perms.USER_WIPE)
    public void wipe(CommandSender sender, @Param(name = "player") Profile profile) {
        PacketHandler.send(new KickPlayerPacket(profile.getUuid(), "&cYour profile has been wiped."));
        Holiday.getInstance().getApi().deleteProfile(profile);
        sender.sendMessage(CC.translate("&cWiped " + profile.getName() + "'s profile."));
    }

    @SubCommand(names = {"uuid"}, description = "Get a player's UUID.", permission = Perms.USER_RESOLVE, async = true)
    public void uuid(CommandSender sender, @Param(name = "player") String playerName) {
        sender.sendMessage(CC.CHAT + "Resolving UUID for " + playerName + "...");
        UUID cached = Holiday.getInstance().getUuidCache().uuid(playerName);
        if (cached != null) {
            sender.sendMessage(CC.CHAT + playerName + "'s UUID is " + cached);
            return;
        }
        UUIDFetcher.get(playerName).whenCompleteAsync((uuid, ignored) -> {
            if (uuid == null) {
                sender.sendMessage(CC.CHAT + "Could not resolve UUID for " + playerName + ".");
                return;
            }
            sender.sendMessage(CC.CHAT + playerName + "'s UUID is " + uuid);
        });
    }

    @SubCommand(names = {"name"}, description = "Get a player's name.", permission = Perms.USER_RESOLVE, async = true)
    public void name(CommandSender sender, @Param(name = "uuid") UUID uuid) {
        sender.sendMessage(CC.CHAT + "Resolving name for " + uuid + "...");
        String cached = Holiday.getInstance().getUuidCache().name(uuid);
        if (cached != null) {
            sender.sendMessage(CC.CHAT + uuid + "'s name is " + cached);
            return;
        }
        UUIDFetcher.getName(uuid).whenCompleteAsync((name, ignored) -> {
            if (name == null) {
                sender.sendMessage(CC.CHAT + "Could not resolve name for " + uuid + ".");
                return;
            }
            sender.sendMessage(CC.CHAT + uuid + "'s name is " + name);
        });
    }


}
