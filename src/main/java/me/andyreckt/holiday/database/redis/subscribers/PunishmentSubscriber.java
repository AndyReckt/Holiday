package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.PunishmentPacket;
import me.andyreckt.holiday.other.enums.PunishmentSubType;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.player.punishments.PunishData;
import me.andyreckt.holiday.player.punishments.PunishmentHandler;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.StringUtil;
import me.andyreckt.holiday.utils.Tasks;
import me.andyreckt.holiday.utils.TimeUtil;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;
import org.bukkit.Bukkit;

import java.util.UUID;

public class PunishmentSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void add(PunishmentPacket packet) {
        if (!(packet.getSubType() == PunishmentSubType.ADD)) return;

        PunishData data = packet.getPunishData();
        PunishmentHandler ph = Holiday.getInstance().getPunishmentHandler();
        ProfileHandler prh = Holiday.getInstance().getProfileHandler();
        BasicConfigurationFile messages = Holiday.getInstance().getMessages();

        if (!ph.cacheContains(data.getId())) ph.updateCache(data.getId(), data);

        String string = "";
        String message = "";
        boolean kick = false;

        switch (data.getType()) {
            case BAN: {
                string = messages.getString("PUNISHMENTS.MESSAGES.BAN");
                kick = true;
                message = messages.getString("PUNISHMENTS.KICKMESSAGE.BAN");
                break;
            }

            case KICK: {
                string = messages.getString("PUNISHMENTS.MESSAGES.KICK");
                kick = true;
                message = messages.getString("PUNISHMENTS.KICKMESSAGE.KICK");
                break;
            }

            case MUTE: {
                string = messages.getString("PUNISHMENTS.MESSAGES.MUTE");
                break;
            }

            case IP_BAN: {
                string = messages.getString("PUNISHMENTS.MESSAGES.IPBAN");
                kick = true;
                message = messages.getString("PUNISHMENTS.KICKMESSAGE.IPBAN");
                break;
            }

            case TEMP_BAN: {
                string = messages.getString("PUNISHMENTS.MESSAGES.TEMPBAN");
                kick = true;
                message = messages.getString("PUNISHMENTS.KICKMESSAGE.TEMPBAN");
                break;
            }

            case TEMP_MUTE: {
                string = messages.getString("PUNISHMENTS.MESSAGES.TEMPMUTE");
                break;
            }

            case BLACKLIST: {
                string = messages.getString("PUNISHMENTS.MESSAGES.BLACKLIST");
                kick = true;
                message = messages.getString("PUNISHMENTS.KICKMESSAGE.BLACKLIST");
                break;
            }
        }

        Profile issuer = data.getAddedBy();
        Profile punished = data.getPunished();

        string = StringUtil.addNetworkPlaceholder(string);
        message = StringUtil.addNetworkPlaceholder(message);

        string = string.replace("<executor>", issuer.getNameWithColor());
        string = string.replace("<player>", punished.getNameWithColor());
        string = string.replace("<reason>", data.getAddedReason());
        string = string.replace("<duration>", TimeUtil.getDuration(data.getDuration()));

        if (data.isSilent()) {
            String fString = message = messages.getString("PUNISHMENTS.SILENTPREFIX") + string;
            prh.getOnlineProfiles().forEach(profile -> {
                if (profile.isStaff()) {
                    profile.getPlayer().sendMessage(CC.translate(fString));
                    Holiday.getInstance().getLogger().info(CC.translate(fString));
                }
            });
        } else {
            Bukkit.broadcastMessage(CC.translate(string));
        }

        if ((Bukkit.getPlayer(data.getPunished().getUuid()) != null) && kick) {
            message = message.replace("<executor>", issuer.getNameWithColor());
            message = message.replace("<player>", punished.getNameWithColor());
            message = message.replace("<reason>", data.getAddedReason());
            message = message.replace("<duration>", TimeUtil.getDuration(data.getDuration()));
            kickPlayer(data.getPunished().getUuid(), message);
            Holiday.getInstance().infoConsole(message);
        }

    }

    @IncomingPacketHandler
    public void remove(PunishmentPacket packet) {
        if (!(packet.getSubType() == PunishmentSubType.REMOVE) || packet.getSubType().equals(PunishmentSubType.REMOVESILENT)) return;

        PunishmentHandler ph = Holiday.getInstance().getPunishmentHandler();
        PunishData data = packet.getPunishData();
        BasicConfigurationFile messages = Holiday.getInstance().getMessages();

        ph.updateCache(data.getId(), data);

        String message = "";
        switch (data.getType()) {
            case UNBAN: {
                message = messages.getString("PUNISHMENTS.MESSAGES.UNBAN");
                break;
            }
            case UNBLACKLIST: {
                message = messages.getString("PUNISHMENTS.MESSAGES.UNIPBAN");
                break;
            }
            case UNMUTE: {
                message = messages.getString("PUNISHMENTS.MESSAGES.UNBLACKLIST");
                break;
            }
        }

        Profile issuer = data.getAddedBy();
        Profile punished = data.getPunished();
        message = StringUtil.addNetworkPlaceholder(message);

        message = message.replace("<executor>", issuer.getNameWithColor());
        message = message.replace("<player>", punished.getNameWithColor());
        message = message.replace("<reason>", data.getAddedReason());
        message = message.replace("<duration>", TimeUtil.getDuration(data.getDuration()));

        if (packet.getSubType() == PunishmentSubType.REMOVESILENT) {
            String finalMessage = message;
            Holiday.getInstance().getProfileHandler().getOnlineProfiles().stream()
                    .filter(Profile::isStaff)
                    .map(Profile::getPlayer)
                    .forEach(player -> player.sendMessage(CC.translate(messages.getString("PUNISHMENTS.SILENTPREFIX") + finalMessage)));
        } else {
            Bukkit.broadcastMessage(CC.translate(message));
        }
    }

    @IncomingPacketHandler
    public void edit(PunishmentPacket packet) {
        if (!(packet.getSubType() == PunishmentSubType.EDIT)) return;
        Holiday.getInstance().getPunishmentHandler().updateCache(packet.getPunishData().getId(), packet.getPunishData());
    }

    void kickPlayer(UUID uuid, String message) {
        Tasks.run(() -> Bukkit.getPlayer(uuid).kickPlayer(CC.translate(message)));
    }
}
