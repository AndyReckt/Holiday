package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.KickPlayerPacket;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
  
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.user.punishment.Punishment;
import me.andyreckt.holiday.core.util.duration.Duration;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class PunishmentCommands extends BaseCommand {

    @CommandAlias("ban|b")
    @CommandCompletion("@players @nothing")
    @CommandPermission("core.command.ban")
    public void ban(CommandSender sender,
                                @Name("name") @Single Profile target,
                                @Name("reason") @Default("Cheating") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        reason = reason.replace("-s", "");
        if (reason.equals("") || reason.equals(" ")) reason = "Cheating";

        punish(profile, target, IPunishment.PunishmentType.BAN, Duration.PERMANENT, reason, silent, sender);
    }

    @CommandCompletion("@players @nothing")
    @CommandPermission("core.command.blacklist")
    @CommandAlias("blacklist|bl")
    public void blacklist(CommandSender sender,
                                 @Name("name") @Single Profile target,
                                 @Name("reason") @Default("Cheating") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        reason = reason.replace("-s", "");
        if (reason.equals("") || reason.equals(" ")) reason = "Cheating";

        punish(profile, target, IPunishment.PunishmentType.BLACKLIST, Duration.PERMANENT, reason, silent, sender);
    }

    @CommandAlias("ipban|ipb|banip|ban-ip")
    @CommandCompletion("@players @nothing")
    @CommandPermission("core.command.ipban")
    public void ipban(CommandSender sender,
                             @Name("name") @Single Profile target,
                             @Name("reason") @Default("Cheating") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        reason = reason.replace("-s", "");
        if (reason.equals("") || reason.equals(" ")) reason = "Cheating";

        punish(profile, target, IPunishment.PunishmentType.IP_BAN, Duration.PERMANENT, reason, silent, sender);
    }

    @CommandAlias("tempban|tban|tb")
    @CommandCompletion("@players @nothing @nothing")
    @CommandPermission("core.command.tempban")
    public void tempban(CommandSender sender,
                               @Name("name") @Single Profile target,
                               @Name("time") @Single Duration duration,
                               @Name("reason") @Default("Cheating") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        reason = reason.replace("-s", "");
        if (reason.equals("") || reason.equals(" ")) reason = "Cheating";

        punish(profile, target, IPunishment.PunishmentType.BAN, duration, reason, silent, sender);
    }

    @CommandAlias("mute")
    @CommandCompletion("@players @nothing")
    @CommandPermission("core.command.mute")
    public void mute(CommandSender sender,
                                @Name("name") @Single Profile target,
                                @Name("reason") @Default("Cheating") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        reason = reason.replace("-s", "");
        if (reason.equals("") || reason.equals(" ")) reason = "Cheating";

        punish(profile, target, IPunishment.PunishmentType.MUTE, Duration.PERMANENT, reason, silent, sender);
    }

    @CommandAlias("tempmute|tmute")
    @CommandCompletion("@players @nothing @nothing")
    @CommandPermission("core.command.tempmute")
    public void tempmute(CommandSender sender,
                                @Name("name") @Single Profile target,
                                @Name("time") @Single Duration duration,
                                @Name("reason") @Default("Cheating") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        reason = reason.replace("-s", "");
        if (reason.equals("") || reason.equals(" ")) reason = "Cheating";

        punish(profile, target, IPunishment.PunishmentType.MUTE, duration, reason, silent, sender);
    }

    @CommandCompletion("@players @nothing")
    @CommandPermission("core.command.kick")
    @CommandAlias("kick")
    public void kick(CommandSender sender,
                     @Name("name") @Single Profile target,
                     @Name("reason") @Default("Misconduct") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        reason = reason.replace("-s", "");
        if (reason.equals("") || reason.equals(" ")) reason = "Cheating";

        String issuerName = UserConstants.getNameWithColor(profile);
        String targetName = UserConstants.getDisplayNameWithColor(target);

        String kickBroadcast = Locale.PUNISHMENT_KICK_MESSAGE.getString();


        kickBroadcast = kickBroadcast.replace("%executor%", issuerName)
                .replace("%player%", targetName)
                .replace("%silent%", silent ? Locale.PUNISHMENT_SILENT_PREFIX.getString() : "")
                .replace("%reason%", reason);

        if (!silent) {
            PacketHandler.send(new BroadcastPacket(kickBroadcast));
        } else {
            PacketHandler.send(new BroadcastPacket(
                    kickBroadcast,
                    Perms.PUNISHMENTS_SILENT_VIEW.get(),
                    AlertType.SILENT_PUNISHMENT));
        }

        String toSend = Locale.PUNISHMENT_KICK_KICK_MESSAGE.getStringNetwork().replace("%reason%", reason);
        PacketHandler.send(new KickPlayerPacket(target.getUuid(), toSend));
    }


    private void punish(Profile issuer, Profile target, IPunishment.PunishmentType punishmentType, Duration duration, String reason, boolean silent, CommandSender sender) {
        if (!issuer.getHighestRank().isAboveOrEqual(target.getHighestRank())
                && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(Locale.CANNOT_PUNISH_PLAYER.getString());
            return;
        }

        if (alreadyPunished(target, punishmentType)) {
            sender.sendMessage(Locale.PLAYER_ALREADY_PUNISHED.getString());
            return;
        }
        Punishment punishment = new Punishment(
                target.getUuid(),
                punishmentType,
                duration,
                issuer.getUuid(),
                reason,
                Holiday.getInstance().getThisServer().getServerName()
        );
        Holiday.getInstance().getApi().savePunishment(punishment);

        sendPunishmentBroadcast(punishment, silent);
        kickPlayer(punishment);
    }

    private void kickPlayer(Punishment punishment) {
        if (punishment.getType() == IPunishment.PunishmentType.MUTE) return;
        String toSend = "";
        switch (punishment.getType()) {
            case BAN:
                toSend = punishment.getDuration() == TimeUtil.PERMANENT ? Locale.PUNISHMENT_BAN_KICK.getStringNetwork() : Locale.PUNISHMENT_TEMP_BAN_KICK.getStringNetwork();
                break;
            case IP_BAN:
                toSend = Locale.PUNISHMENT_IP_BAN_KICK.getStringNetwork();
                break;
            case BLACKLIST:
                toSend = Locale.PUNISHMENT_BLACKLIST_KICK.getStringNetwork();
                break;
        }
        toSend = toSend.replace("%reason%", punishment.getAddedReason())
                .replace("%duration%", punishment.getDurationObject().toRoundedTime());
        PacketHandler.send(new KickPlayerPacket(punishment.getPunished(), toSend));

        if (punishment.getType() != IPunishment.PunishmentType.BAN) return;

        Profile punished = Holiday.getInstance().getApi().getProfile(punishment.getPunished());

        for (UUID alt : punished.getAlts()) {
            String altToSend = Locale.PUNISHMENT_BLACKLIST_KICK_RELATED.getStringNetwork();
            altToSend = altToSend.replace("%reason%", punishment.getAddedReason())
                    .replace("%duration%", punishment.getDurationObject().toRoundedTime())
                    .replace("%related%", UserConstants.getNameWithColor(punished));
            PacketHandler.send(new KickPlayerPacket(alt, altToSend));
        }
    }

    private void sendPunishmentBroadcast(Punishment punishment, boolean silent) {
        String toSend = "";
        switch (punishment.getType()) {
            case BAN:
                toSend = punishment.getDurationObject().isPermanent() ? Locale.PUNISHMENT_BAN_MESSAGE.getString() : Locale.PUNISHMENT_TEMP_BAN_MESSAGE.getString();
                break;
            case MUTE:
                toSend = punishment.getDurationObject().isPermanent() ? Locale.PUNISHMENT_MUTE_MESSAGE.getString() : Locale.PUNISHMENT_TEMP_MUTE_MESSAGE.getString();
                break;
            case BLACKLIST:
                toSend = Locale.PUNISHMENT_BLACKLIST_MESSAGE.getString();
                break;
            case IP_BAN:
                toSend = Locale.PUNISHMENT_IP_BAN_MESSAGE.getString();
                break;
        }

        if (toSend.equals("")) return;

        Profile issuer = Holiday.getInstance().getApi().getProfile(punishment.getAddedBy());
        Profile target = Holiday.getInstance().getApi().getProfile(punishment.getPunished());

        String issuerName = UserConstants.getNameWithColor(issuer);
        String targetName = UserConstants.getDisplayNameWithColor(target);

        toSend = toSend.replace("%executor%", issuerName)
                .replace("%player%", targetName)
                .replace("%silent%", silent ? Locale.PUNISHMENT_SILENT_PREFIX.getString() : "")
                .replace("%reason%", punishment.getAddedReason())
                .replace("%duration%", punishment.getDurationObject().getFormatted());

        if (!silent) {
            PacketHandler.send(new BroadcastPacket(toSend));
        } else {
            PacketHandler.send(new BroadcastPacket(
                    toSend,
                    Perms.PUNISHMENTS_SILENT_VIEW.get(),
                    AlertType.SILENT_PUNISHMENT));
        }
    }

    private boolean alreadyPunished(Profile target, IPunishment.PunishmentType type) {
        switch (type) {
            case MUTE: {
                return target.isMuted();
            }
            case BAN: {
                return target.isBanned();
            }
            case IP_BAN: {
                return target.isIpBanned();
            }
            case BLACKLIST: {
                return target.isBlacklisted();
            }
        }
        return false;
    }

}
