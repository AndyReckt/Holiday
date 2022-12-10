package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.PunishmentPacket;
import me.andyreckt.holiday.other.enums.PunishmentSubType;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.player.punishments.PunishData;
import me.andyreckt.holiday.player.punishments.PunishmentHandler;
import me.andyreckt.holiday.player.punishments.PunishmentType;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.StringUtil;
import me.andyreckt.holiday.utils.Tasks;
import me.andyreckt.holiday.utils.TimeUtil;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PunishmentSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void add(PunishmentPacket packet) {
        if (!(packet.getSubType() == PunishmentSubType.ADD)) return;

        PunishData data = packet.getPunishData();
        PunishmentHandler ph = Holiday.getInstance().getPunishmentHandler();

        if (!ph.cacheContains(data.getId())) ph.updateCache(data.getId(), data);

        handleBroadcast(packet.getPunishData(), packet.getType(), packet.getPunishData().isSilent(), false);
        handleKick(packet.getPunishData(), packet.getType());
    }

    @IncomingPacketHandler
    public void remove(PunishmentPacket packet) {
        if (!(packet.getSubType() == PunishmentSubType.REMOVE || packet.getSubType().equals(PunishmentSubType.REMOVESILENT))) return;

        PunishmentHandler ph = Holiday.getInstance().getPunishmentHandler();
        PunishData data = packet.getPunishData();

        ph.updateCache(data.getId(), data);

        handleBroadcast(data, packet.getType(), packet.getSubType() == PunishmentSubType.REMOVESILENT, true);
    }

    @IncomingPacketHandler
    public void edit(PunishmentPacket packet) {
        if (!(packet.getSubType() == PunishmentSubType.EDIT)) return;
        Holiday.getInstance().getPunishmentHandler().updateCache(packet.getPunishData().getId(), packet.getPunishData());
    }



    private void handleBroadcast(PunishData data, PunishmentType type, boolean silent, boolean remove) {
        String string = "";
        switch (type) {
            case BAN: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.MESSAGES.BAN");
                break;
            }
            case KICK: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.MESSAGES.KICK");
                break;
            }
            case MUTE: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.MESSAGES.MUTE");
                break;
            }
            case IP_BAN: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.MESSAGES.IPBAN");
                break;
            }
            case UNIP_BAN: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.MESSAGES.UNIPBAN");
                break;
            }
            case UNBAN: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.MESSAGES.UNBAN");
                break;
            }
            case UNMUTE: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.MESSAGES.UNMUTE");
                break;
            }
            case BLACKLIST: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.MESSAGES.BLACKLIST");
                break;
            }
            case TEMP_BAN: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.MESSAGES.TEMPBAN");
                break;
            }
            case TEMP_MUTE: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.MESSAGES.TEMPMUTE");
                break;
            }
        }
        string = StringUtil.addNetworkPlaceholder(string);

        Profile punished = data.getPunished();
        Profile issuer = remove ? data.getRemovedBy() : data.getAddedBy();


        string = string.replace("<executor>", issuer.getNameWithColor());
        string = string.replace("<player>", punished.getNameWithColor());
        string = remove ? string.replace("<reason>", data.getRemovedReason()) : string.replace("<reason>", data.getAddedReason());
        string = string.replace("<duration>", TimeUtil.getDuration(data.getDuration()));

        if (silent) string = string.replace("<silent>", Holiday.getInstance().getMessages().getString("PUNISHMENTS.SILENTPREFIX"));
        else string = string.replace("<silent>", "");

        String finalString = string;

        if (silent) Holiday.getInstance().getProfileHandler().getOnlineProfiles().stream()
                .filter(Profile::isStaff)
                .forEach(profile -> profile.getPlayer().sendMessage(CC.translate(finalString)));
        else Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(CC.translate(finalString)));

        Holiday.getInstance().infoConsole(finalString);
    }

    private void handleKick(PunishData data, PunishmentType type) {
        Profile issuer = data.getAddedBy();
        Profile punished = data.getPunished();
        boolean kick = true;

        if (Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).noneMatch(uuid -> uuid.equals(punished.getUuid()))) return;
        String string = "";
        switch (type) {
            case TEMP_BAN: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.KICKMESSAGE.TEMPBAN");
                break;
            }
            case BLACKLIST: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.KICKMESSAGE.BLACKLIST");
                break;
            }
            case KICK: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.KICKMESSAGE.KICK");
                break;
            }
            case BAN: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.KICKMESSAGE.BAN");
                break;
            }
            case IP_BAN: {
                string = Holiday.getInstance().getMessages().getString("PUNISHMENTS.KICKMESSAGE.IPBAN");
                break;
            }
            default: {
                kick = false;
                break;
            }
        }
        if (!kick) return;

        string = StringUtil.addNetworkPlaceholder(string);
        string = string.replace("<executor>", issuer.getNameWithColor());
        string = string.replace("<player>", punished.getNameWithColor());
        string = string.replace("<reason>", data.getAddedReason());
        string = string.replace("<duration>", TimeUtil.getDuration(data.getDuration()));
        kickPlayer(punished.getUuid(), string);
    }

    private void kickPlayer(UUID uuid, String message) {
        Tasks.run(() -> Bukkit.getPlayer(uuid).kickPlayer(CC.translate(message)));
    }
}
