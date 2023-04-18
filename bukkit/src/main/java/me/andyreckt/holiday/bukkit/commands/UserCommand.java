package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.KickPlayerPacket;
import me.andyreckt.holiday.bukkit.server.redis.packet.PermissionUpdatePacket;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
 
  
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.http.UUIDFetcher;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("user|u")
@CommandPermission("core.command.user")
public class UserCommand extends BaseCommand {

    @HelpCommand
    @Syntax("[page]")
    public void doHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("addpermission|addperm")
    public void addPermission(CommandSender sender, @Single @Name("player") Profile profile, @Single @Name("permission") String permission) {
        if (profile.getPermissions().contains(permission)) {
            sender.sendMessage(Locale.PLAYER_ALREADY_HAS_PERMISSION.getString());
            return;
        }
        profile.addPermission(permission);
        sender.sendMessage("Added permission " + permission + " to " + profile.getName() + ".");
        Holiday.getInstance().getApi().saveProfile(profile);
        PacketHandler.send(new PermissionUpdatePacket(profile.getUuid()));
    }

    @Subcommand("removepermission|removeperm|remperm")
    public void removePermission(CommandSender sender, @Single @Name("player") Profile profile, @Single @Name("permission") String permission) {
        if (!profile.getPermissions().contains(permission)) {
            sender.sendMessage(Locale.PLAYER_DOES_NOT_HAVE_PERMISSION.getString());
            return;
        }

        profile.removePermission(permission);
        sender.sendMessage("Removed permission " + permission + " from " + profile.getName() + ".");
        Holiday.getInstance().getApi().saveProfile(profile);
        PacketHandler.send(new PermissionUpdatePacket(profile.getUuid()));
    }

    @Subcommand("wipe|delete")
    @CommandPermission("core.command.user.wipe")
    public void wipe(CommandSender sender, @Single @Name("player") Profile profile) {
        PacketHandler.send(new KickPlayerPacket(profile.getUuid(), "&cYour profile has been wiped."));
        Holiday.getInstance().getApi().deleteProfile(profile);
        sender.sendMessage(CC.translate("&cWiped " + profile.getName() + "'s profile."));
    }

    @Subcommand("uuid")
    @CommandPermission("core.command.user.resolve")
    public void uuid(CommandSender sender, @Single @Name("player") String playerName) {
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

    @CommandPermission("core.command.user.resolve")
    @Subcommand("name")
    public void name(CommandSender sender, @Name("uuid") UUID uuid) {
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
