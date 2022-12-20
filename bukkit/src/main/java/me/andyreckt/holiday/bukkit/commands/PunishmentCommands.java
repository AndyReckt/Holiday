package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.user.punishment.Punishment;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PunishmentCommands {

    @Command(names = {"ban", "b"}, async = true, permission = Perms.BAN)
    public void ban(CommandSender sender,
                           @Param(name = "name") Profile target,
                           @Param(name = "reason", wildcard = true, baseValue = "Cheating") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("") || fReason.equals(" ")) fReason = "Cheating";

        punish(profile, target, IPunishment.PunishmentType.BAN, TimeUtil.PERMANENT, fReason, silent, sender);
    }

    @Command(names = {"blacklist", "bl"}, async = true, permission = Perms.BLACKLIST)
    public void blacklist(CommandSender sender,
                                 @Param(name = "name") Profile target,
                                 @Param(name = "reason", wildcard = true, baseValue = "Cheating") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("") || fReason.equals(" ")) fReason = "Cheating";

        punish(profile, target, IPunishment.PunishmentType.BAN, TimeUtil.PERMANENT, fReason, silent, sender);
    }

    @Command(names = {"ipban", "ipb", "banip", "ban-ip"}, async = true, permission = Perms.IPBAN)
    public void ipban(CommandSender sender,
                             @Param(name = "name") Profile target,
                             @Param(name = "reason", wildcard = true, baseValue = "Cheating") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("") || fReason.equals(" ")) fReason = "Cheating";

        punish(profile, target, IPunishment.PunishmentType.BAN, TimeUtil.PERMANENT, fReason, silent, sender);
    }

    @Command(names = {"tempban", "tban", "tb"}, async = true, permission = Perms.TEMPBAN)
    public void tempban(CommandSender sender,
                               @Param(name = "name") Profile target,
                               @Param(name = "time") String duration,
                               @Param(name = "reason", wildcard = true, baseValue = "Cheating") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("") || fReason.equals(" ")) fReason = "Cheating";

        punish(profile, target, IPunishment.PunishmentType.BAN, TimeUtil.getDuration(duration), fReason, silent, sender);
    }

    @Command(names = {"mute"}, async = true, permission = Perms.MUTE)
    public void mute(CommandSender sender,
                            @Param(name = "name") Profile target,
                            @Param(name = "reason", wildcard = true, baseValue = "Cheating") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("") || fReason.equals(" ")) fReason = "Cheating";

        punish(profile, target, IPunishment.PunishmentType.BAN, TimeUtil.PERMANENT, fReason, silent, sender);
    }

    @Command(names = {"tempmute", "tmute"}, async = true, permission = Perms.TEMPMUTE)
    public void tempmute(CommandSender sender,
                                @Param(name = "name") Profile target,
                                @Param(name = "time") String duration,
                                @Param(name = "reason", wildcard = true, baseValue = "Cheating") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("") || fReason.equals(" ")) fReason = "Cheating";

        punish(profile, target, IPunishment.PunishmentType.BAN, TimeUtil.getDuration(duration), fReason, silent, sender);
    }


    private void punish(Profile issuer, Profile target, IPunishment.PunishmentType punishmentType, long duration, String reason, boolean silent, CommandSender sender) {
        if (!issuer.getHighestRank().isAboveOrEqual(target.getHighestRank())
                && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(Locale.CANNOT_PUNISH_PLAYER.getString());
            return;
        }

        if (alreadyPunished(target, punishmentType)) {
            sender.sendMessage(Locale.PLAYER_ALREADY_PUNISHED.getString());
            return;
        }
        IPunishment punishment = new Punishment(
                target.getUuid(),
                punishmentType,
                duration,
                issuer.getUuid(),
                reason
        );
        Holiday.getInstance().getApi().savePunishment(punishment);

        sendPunishmentBroadcast(punishment, silent);
    }

    private void sendPunishmentBroadcast(IPunishment punishment, boolean silent) {
        String toSend = "";
        switch (punishment.getType()) {
            case BAN:
                toSend = Locale.PUNISHMENT_BAN_MESSAGE.getString();
                break;
            case MUTE:
                toSend = Locale.PUNISHMENT_MUTE_MESSAGE.getString();
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

        String issuerName = issuer == UserProfile.getConsoleProfile() ? "&4Console" : Holiday.getInstance().getNameWithColor(issuer);
        String targetName = Holiday.getInstance().getNameWithColor(target);

        toSend = toSend.replace("%executor%", issuerName)
                .replace("%player%", targetName)
                .replace("%silent%", silent ? Locale.PUNISHMENT_SILENT_PREFIX.getString() : "")
                .replace("%reason%", punishment.getAddedReason())
                .replace("%duration%", TimeUtil.getDuration(punishment.getDuration()));

        if (!silent) {
            Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(toSend));
        } else {
            Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(
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
