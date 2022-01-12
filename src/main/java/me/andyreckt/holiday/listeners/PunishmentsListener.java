package me.andyreckt.holiday.listeners;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.StaffMessages;
import me.andyreckt.holiday.other.enums.StaffMessageType;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.punishments.PunishData;
import me.andyreckt.holiday.player.punishments.PunishmentHandler;
import me.andyreckt.holiday.player.punishments.PunishmentType;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.StringUtil;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.concurrent.atomic.AtomicReference;


@SuppressWarnings("unused")
public class PunishmentsListener implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoinHandle(PlayerLoginEvent event) {
        Profile profile = Holiday.getInstance().getProfileHandler().getByUUID(event.getPlayer().getUniqueId());
        PunishmentHandler ph = Holiday.getInstance().getPunishmentHandler();
        BasicConfigurationFile messages = Holiday.getInstance().getMessages();

        AtomicReference<String> message = new AtomicReference<>("");
        AtomicReference<PunishData> activePunishment = new AtomicReference<>(null);

        String punish = null;
        PunishData punishData = null;


        for (PunishData punishment : ph.getAllPunishmentsPlayer(event.getPlayer())) {
            if (!punishment.isActive()) return;
            switch (punishment.getType()) {
                case TEMP_BAN: {
                    if (activePunishment.get() != null && (punishment.getType() == PunishmentType.IP_BAN
                            || punishment.getType() == PunishmentType.BAN
                            || punishment.getType() == PunishmentType.BLACKLIST)) continue;
                    message.set(messages.getString("PUNISHMENTS.KICKMESSAGE.TEMPBAN"));
                    activePunishment.set(punishment);
                    break;
                }
                case BAN: {
                    if (activePunishment.get() != null && (punishment.getType() == PunishmentType.IP_BAN
                            || punishment.getType() == PunishmentType.BLACKLIST)) continue;
                    message.set(messages.getString("PUNISHMENTS.KICKMESSAGE.BAN"));
                    activePunishment.set(punishment);
                    break;
                }
                case IP_BAN: {
                    if (activePunishment.get() != null && punishment.getType() == PunishmentType.BLACKLIST)
                        continue;
                    message.set(messages.getString("PUNISHMENTS.KICKMESSAGE.IPBAN"));
                    activePunishment.set(punishment);
                    break;
                }
                case BLACKLIST: {
                    message.set(messages.getString("PUNISHMENTS.KICKMESSAGE.BLACKLIST"));
                    activePunishment.set(punishment);
                    break;
                }
            }
            if (activePunishment.get() != null) {
                punishData = activePunishment.get();
                String string = CC.translate(message.get());
                string = string.replace("<executor>", punishData.getAddedBy().getName());
                string = string.replace("<reason>", punishData.getAddedReason());
                string = string.replace("<duration>", punishData.getNiceDuration());
                string = string.replace("<executor>", punishData.getAddedBy().getName());
                punish = string;
            }
        }

        for (PunishData data : profile.getPunishments()) {
            if (!data.isActive()) return;
            switch (data.getType()) {
                case TEMP_BAN: {
                    if (data.getPunished().getUuid().toString().equals(profile.getUuid().toString())) {
                        message.set(messages.getString("PUNISHMENTS.KICKMESSAGE.TEMPBAN"));
                        activePunishment.set(data);
                    } else {
                        Holiday.getInstance().getRedis().sendPacket(new StaffMessages.StaffMessagesPacket(
                                Holiday.getInstance().getMessages().getString("PUNISHMENTS.NOTIFY.ALT").replace("<player>", profile.getName()).replace("<alt>", data.getPunished().getName()),
                                StaffMessageType.ADMIN
                        ));
                    }
                    break;
                }
                case BAN: {
                    if (data.getPunished().getUuid().toString().equals(profile.getUuid().toString())) {
                        message.set(messages.getString("PUNISHMENTS.KICKMESSAGE.BAN"));
                        activePunishment.set(data);
                    } else {
                        Holiday.getInstance().getRedis().sendPacket(new StaffMessages.StaffMessagesPacket(
                                Holiday.getInstance().getMessages().getString("PUNISHMENTS.NOTIFY.ALT").replace("<player>", profile.getName()).replace("<alt>", data.getPunished().getName()),
                                StaffMessageType.ADMIN
                        ));
                    }
                    break;
                }
                case IP_BAN: {
                    message.set(messages.getString("PUNISHMENTS.KICKMESSAGE.IPBAN"));
                    activePunishment.set(data);
                    break;
                }
                case BLACKLIST: {
                    message.set(messages.getString("PUNISHMENTS.KICKMESSAGE.BLACKLIST"));
                    activePunishment.set(data);
                    break;
                }
            }

            if (activePunishment.get() == null) return;

            punishData = activePunishment.get();
            String string = message.get();
            string = string.replace("<executor>", punishData.getAddedBy().getName());
            string = string.replace("<reason>", punishData.getAddedReason());
            string = string.replace("<duration>", punishData.getNiceDuration());
            string = string.replace("<executor>", punishData.getAddedBy().getName());
            punish = string;
        }

        if (punish == null) return;
        if (!(punishData.getType() == PunishmentType.BAN || punishData.getType() == PunishmentType.TEMP_BAN)) return;
        if (Holiday.getInstance().getSettings().getBoolean("SERVER.BANNEDJOIN")) return;

        handleBan(event, punish);

    }

    void handleBan(PlayerLoginEvent event, String message) {
        event.setKickMessage(CC.translate(StringUtil.addNetworkPlaceholder(message)));
        event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
        Holiday.getInstance().getRedis().sendPacket(new StaffMessages.StaffMessagesPacket(
                Holiday.getInstance().getMessages().getString("PUNISHMENTS.NOTIFY.BAN").replace("<player>", event.getPlayer().getName()),
                StaffMessageType.ADMIN
        ));
    }

}
