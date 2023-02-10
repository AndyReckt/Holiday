package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PunishmentRemoveCommands {

    @Command(names = {"unban", "pardon"}, async = true, permission = Perms.UNBAN)
    public void unban(CommandSender sender, @Param(name = "player") Profile target, @Param(name = "reason", wildcard = true, baseValue = "Appealed") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("") || fReason.equals(" ")) fReason = "Appealed";

        unPunish(profile, target, IPunishment.PunishmentType.BAN, fReason, silent, sender);
    }

    @Command(names = "unipban", async = true, permission = Perms.UNIPBAN)
    public void unipban(CommandSender sender, @Param(name = "player") Profile target, @Param(name = "reason", wildcard = true, baseValue = "Appealed") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("") || fReason.equals(" ")) fReason = "Appealed";

        unPunish(profile, target, IPunishment.PunishmentType.IP_BAN, fReason, silent, sender);
    }

    @Command(names = "unmute", async = true, permission = Perms.UNMUTE)
    public void unmute(CommandSender sender, @Param(name = "player") Profile target, @Param(name = "reason", wildcard = true, baseValue = "Appealed") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("") || fReason.equals(" ")) fReason = "Appealed";

        unPunish(profile, target, IPunishment.PunishmentType.MUTE, fReason, silent, sender);
    }

    @Command(names = "unblacklist", async = true, permission = Perms.UNBLACKLIST)
    public void unblacklist(CommandSender sender, @Param(name = "player") Profile target, @Param(name = "reason", wildcard = true, baseValue = "Appealed") String reason) {
        API api = Holiday.getInstance().getApi();
        Profile profile = sender instanceof Player ? api.getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();

        boolean silent = reason.contains("-s") || reason.endsWith("-s");
        String fReason = reason.replace("-s", "");
        if (fReason.equals("") || fReason.equals(" ")) fReason = "Appealed";

        unPunish(profile, target, IPunishment.PunishmentType.BLACKLIST, fReason, silent, sender);
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
