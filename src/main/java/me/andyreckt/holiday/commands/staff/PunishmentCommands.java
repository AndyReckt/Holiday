package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.punishments.Punishment;
import me.andyreckt.holiday.player.punishments.PunishmentType;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.CommandUtils;
import me.andyreckt.holiday.utils.PunishmentUtils;
import me.andyreckt.holiday.utils.TimeUtil;
import me.andyreckt.sunset.annotations.Command;
import me.andyreckt.sunset.annotations.Param;
import org.bukkit.command.CommandSender;

public class PunishmentCommands {

    @Command(names = {"ban", "b"}, async = true, permission = "holiday.ban")
    public static void ban(CommandSender sender,
                           @Param(name = "name") Profile target,
                           @Param(name = "reason", wildcard = true, baseValue = "Cheating") String reason) {

        Profile profile = Holiday.getInstance().getProfileHandler().getByCommandSender(sender);
        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("")) fReason = "Cheating";

        punish(profile, target, "ban", PunishmentType.BAN, fReason, silent, sender);
    }

    @Command(names = {"blacklist", "bl"}, async = true, permission = "holiday.blacklist")
    public static void blacklist(CommandSender sender,
                                 @Param(name = "name") Profile target,
                                 @Param(name = "reason", wildcard = true, baseValue = "Cheating") String reason) {

        Profile profile = Holiday.getInstance().getProfileHandler().getByCommandSender(sender);
        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("")) fReason = "Cheating";

        punish(profile, target, "blacklist", PunishmentType.BLACKLIST, fReason, silent, sender);
    }

    @Command(names = {"ipban", "ipb", "banip", "ban-ip"}, async = true, permission = "holiday.ipban")
    public static void ipban(CommandSender sender,
                             @Param(name = "name") Profile target,
                             @Param(name = "reason", wildcard = true, baseValue = "Cheating") String reason) {

        Profile profile = Holiday.getInstance().getProfileHandler().getByCommandSender(sender);
        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("")) fReason = "Cheating";

        punish(profile, target, "ip-ban", PunishmentType.IP_BAN, fReason, silent, sender);
    }

    @Command(names = {"tempban", "tban", "tb"}, async = true, permission = "holiday.tempban")
    public static void tempban(CommandSender sender,
                               @Param(name = "name") Profile target,
                               @Param(name = "time") String duration,
                               @Param(name = "reason", wildcard = true, baseValue = "Cheating") String reason) {

        Profile profile = Holiday.getInstance().getProfileHandler().getByCommandSender(sender);
        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("")) fReason = "Cheating";

        punish(profile, target, "tempban", PunishmentType.TEMP_BAN, duration, fReason, silent, sender);
    }

    @Command(names = {"mute"}, async = true, permission = "holiday.mute")
    public static void mute(CommandSender sender,
                            @Param(name = "name") Profile target,
                            @Param(name = "reason", wildcard = true, baseValue = "Cheating") String reason) {

        Profile profile = Holiday.getInstance().getProfileHandler().getByCommandSender(sender);
        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("")) fReason = "Cheating";

        punish(profile, target, "mute", PunishmentType.MUTE, fReason, silent, sender);
    }

    @Command(names = {"tempmute", "tmute"}, async = true, permission = "holiday.tempmute")
    public static void tempmute(CommandSender sender,
                                @Param(name = "name") Profile target,
                                @Param(name = "time") String duration,
                                @Param(name = "reason", wildcard = true, baseValue = "Cheating") String reason) {

        Profile profile = Holiday.getInstance().getProfileHandler().getByCommandSender(sender);
        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("")) fReason = "Cheating";

        punish(profile, target, "tempmute", PunishmentType.TEMP_MUTE, duration, fReason, silent, sender);
    }

    private static void punish(Profile issuer, Profile target, String type, PunishmentType punishmentType, String reason, boolean silent, CommandSender sender) {
        if (!CommandUtils.canPunish(issuer, target)) {
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.PUNISHMENT.CANNOT").replace("<type>", type));
            return;
        }
        if (alreadyPunished(target, punishmentType)) {
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.PUNISHMENT.ALREADY"));
            return;
        }
        new Punishment(issuer, target, punishmentType, reason, silent);
    }

    private static void punish(Profile issuer, Profile target, String type, PunishmentType punishmentType, String duration, String reason, boolean silent, CommandSender sender) {
        long time = TimeUtil.getDuration(duration);
        if (time == 0L || time == -1L) {
            sender.sendMessage(CC.translate("&cInvalid duration."));
            return;
        }
        if (!CommandUtils.canPunish(issuer, target)) {
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.PUNISHMENT.CANNOT").replace("<type>", type));
            return;
        }
        if (alreadyPunished(target, punishmentType)) {
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.PUNISHMENT.ALREADY"));
            return;
        }
        new Punishment(issuer, target, punishmentType, duration, reason, silent);
    }

    private static boolean alreadyPunished(Profile target, PunishmentType type) {
        switch (type) {
            case TEMP_MUTE:
            case MUTE: {
                if (PunishmentUtils.checkMuted(target)) return true;
                break;
            }
            case TEMP_BAN:
            case BAN: {
                if (PunishmentUtils.checkBanned(target) || PunishmentUtils.checkIPBanned(target) || PunishmentUtils.checkBlacklisted(target))
                    return true;
                break;
            }
            case IP_BAN: {
                if (PunishmentUtils.checkBlacklisted(target) || PunishmentUtils.checkIPBanned(target)) return true;
                break;
            }
            case BLACKLIST: {
                if (PunishmentUtils.checkBlacklisted(target)) return true;
                break;
            }
        }
        return false;
    }

}
