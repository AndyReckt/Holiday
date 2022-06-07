package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.PunishmentPacket;
import me.andyreckt.holiday.other.enums.PunishmentSubType;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.punishments.PunishData;
import me.andyreckt.holiday.player.punishments.PunishmentType;
import me.andyreckt.holiday.utils.BungeeUtil;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.PunishmentUtils;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PunishmentRemoveCommands {

    @Command(names = {"unban", "pardon"}, async = true, perm = "holiday.unban")
    public static void unban(CommandSender sender, @Param(name = "player") Profile target, @Param(name = "reason", wildcard = true, defaultValue = "Appealed") String reason) {
        Profile profile = Holiday.getInstance().getProfileHandler().getByCommandSender(sender);
        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("")) fReason = "Appealed";
        unPunish(profile, target, PunishmentType.UNBAN, fReason, silent, sender);
    }
    @Command(names = "unipban", async = true, perm = "holiday.unipban")
    public static void unipban(CommandSender sender, @Param(name = "player") Profile target, @Param(name = "reason", wildcard = true, defaultValue = "Appealed") String reason) {
        Profile profile = Holiday.getInstance().getProfileHandler().getByCommandSender(sender);
        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("")) fReason = "Appealed";
        unPunish(profile, target, PunishmentType.UNIP_BAN, fReason, silent, sender);
    }
    @Command(names = "unmute", async = true, perm = "holiday.unmute")
    public static void unmute(CommandSender sender, @Param(name = "player") Profile target, @Param(name = "reason", wildcard = true, defaultValue = "Appealed") String reason) {
        Profile profile = Holiday.getInstance().getProfileHandler().getByCommandSender(sender);
        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("")) fReason = "Appealed";
        unPunish(profile, target, PunishmentType.UNMUTE, fReason, silent, sender);
    }
    @Command(names = "unblacklist", async = true, perm = "holiday.unblacklist")
    public static void unblacklist(CommandSender sender, @Param(name = "player") Profile target, @Param(name = "reason", wildcard = true, defaultValue = "Appealed") String reason) {
        Profile profile = Holiday.getInstance().getProfileHandler().getByCommandSender(sender);
        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("")) fReason = "Appealed";
        unPunish(profile, target, PunishmentType.UNBLACKLIST, fReason, silent, sender);
    }

    private static void unPunish(Profile profile, Profile target, PunishmentType type, String reason, boolean silent, CommandSender sender) {
        PunishData data = null;
        switch (type) {
            case UNBAN: {
                if (PunishmentUtils.checkBanned(target)) data = PunishmentUtils.getBan(target);
                break;
            }
            case UNMUTE: {
                if (PunishmentUtils.checkMuted(target)) data = PunishmentUtils.getMute(target);
                break;
            }
            case UNBLACKLIST: {
                if (PunishmentUtils.checkBlacklisted(target)) data = PunishmentUtils.getBlacklist(target);
                break;
            }
            case UNIP_BAN: {
                if (PunishmentUtils.checkIPBanned(target)) data = PunishmentUtils.getIPBan(target);
                break;
            }
        }

        if (data == null) {
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.PUNISHMENT.NOTPUNISHED"));
            return;
        }

        data.setRemovedBy(profile);
        data.setRemovedReason(reason);
        data.setRemoved(true);
        data.setRemovedAt(System.currentTimeMillis());
        data.save();
        if (silent) Holiday.getInstance().getRedis().sendPacket(new PunishmentPacket(data, PunishmentSubType.REMOVESILENT));
        else Holiday.getInstance().getRedis().sendPacket(new PunishmentPacket(data, PunishmentSubType.REMOVE));
    }

}
