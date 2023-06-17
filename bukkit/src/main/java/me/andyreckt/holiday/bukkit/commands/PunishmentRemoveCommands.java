package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
 

import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PunishmentRemoveCommands extends BaseCommand {

    @CommandPermission("core.command.unban")
    @CommandAlias("unban|pardon")
    public void unban(CommandSender sender,
                      @Single @Name("player") Profile target,
                      @Name("reason") @Default("Appealed") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        reason = reason.replace("-s", "");
        if (reason.equals("") || reason.equals(" ")) reason = "Appealed";

        unPunish(profile, target, IPunishment.PunishmentType.BAN, reason, silent, sender);
    }

    @CommandPermission("core.command.unipban")
    @CommandAlias("unipban|pardonip")
    public void unipban(CommandSender sender,
                        @Single @Name("player") Profile target,
                        @Name("reason") @Default("Appealed") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        reason = reason.replace("-s", "");
        if (reason.equals("") || reason.equals(" ")) reason = "Appealed";

        unPunish(profile, target, IPunishment.PunishmentType.IP_BAN, reason, silent, sender);
    }

    @CommandAlias("unmute|pardonmute")
    @CommandPermission("core.command.unmute")
    public void unmute(CommandSender sender,
                       @Single @Name("player") Profile target,
                       @Name("reason") @Default("Appealed") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        reason = reason.replace("-s", "");
        if (reason.equals("") || reason.equals(" ")) reason = "Appealed";

        unPunish(profile, target, IPunishment.PunishmentType.MUTE, reason, silent, sender);
    }

    @CommandAlias("unblacklist|pardonblacklist")
    @CommandPermission("core.command.unblacklist")
    public void unblacklist(CommandSender sender,
                            @Single @Name("player") Profile target,
                            @Name("reason") @Default("Appealed") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        reason = reason.replace("-s", "");
        if (reason.equals("") || reason.equals(" ")) reason = "Appealed";

        unPunish(profile, target, IPunishment.PunishmentType.BLACKLIST, reason, silent, sender);
    }

    private void unPunish(Profile profile, Profile target, IPunishment.PunishmentType type, String reason, boolean silent, CommandSender sender) {
        IPunishment data = getData(target, type);

        if (data == null) {
            sender.sendMessage(Locale.PLAYER_NOT_PUNISHED.getString());
            return;
        }

        data.revoke(profile.getUuid(), reason, Holiday.getInstance().getThisServer().getServerName());
        Holiday.getInstance().getApi().savePunishment(data);

        String toSend = "";
        switch (type) {
            case BAN:
            case IP_BAN:
                toSend = Locale.PUNISHMENT_BAN_REVOKED.getString();
                break;
            case MUTE:
                toSend = Locale.PUNISHMENT_MUTE_REVOKED.getString();
                break;
            case BLACKLIST:
                toSend = Locale.PUNISHMENT_BLACKLIST_REVOKED.getString();
                break;
        }

        if (toSend.equals("")) return;

        String issuerName = UserConstants.getNameWithColor(profile);
        String targetName = UserConstants.getDisplayNameWithColor(target);

        toSend = toSend.replace("%executor%", issuerName)
                .replace("%player%", targetName)
                .replace("%silent%", silent ? Locale.PUNISHMENT_SILENT_PREFIX.getString() : "")
                .replace("%reason%", data.getRevokedReason());

        if (!silent) {
            PacketHandler.send(new BroadcastPacket(toSend));
        } else {
            PacketHandler.send(new BroadcastPacket(
                    toSend,
                    Perms.PUNISHMENTS_SILENT_VIEW.get(),
                    AlertType.SILENT_PUNISHMENT));
        }
    }

    private IPunishment getData(Profile target, IPunishment.PunishmentType type) {
        switch (type) {
            case MUTE: {
                return target.getActivePunishments().stream().filter(punishment -> punishment.getType() == IPunishment.PunishmentType.MUTE).findFirst().orElse(null);
            }
            case BAN: {
                return target.getActivePunishments().stream().filter(punishment -> punishment.getType() == IPunishment.PunishmentType.BAN).findFirst().orElse(null);
            }
            case IP_BAN: {
                return target.getActivePunishments().stream().filter(punishment -> punishment.getType() == IPunishment.PunishmentType.IP_BAN).findFirst().orElse(null);
            }
            case BLACKLIST: {
                return target.getActivePunishments().stream().filter(punishment -> punishment.getType() == IPunishment.PunishmentType.BLACKLIST).findFirst().orElse(null);
            }
        }
        return null;
    }
}
