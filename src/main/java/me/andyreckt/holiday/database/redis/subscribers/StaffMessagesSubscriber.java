package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.StaffMessages;
import me.andyreckt.holiday.other.enums.StaffMessageType;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.Clickable;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.ChatColor;

import java.util.Objects;

public class StaffMessagesSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onMsg(StaffMessages.StaffMessagesPacket packet) {

        String msg = CC.translate(packet.getMessage());
        ProfileHandler ph = Holiday.getInstance().getProfileHandler();
        Holiday.getInstance().infoConsole(packet.getMessage());

        if (packet.getClickCmd() != null && !packet.getClickCmd().equalsIgnoreCase("")) {
            Clickable clickable = new Clickable(msg, packet.getHoverMsg(), packet.getClickCmd());
            if (packet.getChannel() == StaffMessageType.STAFF) {
                ph.getOnlineProfiles().stream()
                        .filter(Profile::isStaff)
                        .map(Profile::getPlayer)
                        .filter(Objects::nonNull)
                        .forEach(clickable::sendToPlayer);
            } else {
                ph.getOnlineProfiles().stream()
                        .filter(Profile::isAdmin)
                        .map(Profile::getPlayer)
                        .filter(Objects::nonNull)
                        .forEach(clickable::sendToPlayer);
            }
        } else {
            if (packet.getChannel() == StaffMessageType.STAFF) {
                ph.getOnlineProfiles().stream()
                        .filter(Profile::isStaff)
                        .map(Profile::getPlayer)
                        .filter(Objects::nonNull)
                        .forEach(o -> o.sendMessage(CC.translate(msg)));
            } else {
                ph.getOnlineProfiles().stream()
                        .filter(Profile::isAdmin)
                        .map(Profile::getPlayer)
                        .filter(Objects::nonNull)
                        .forEach(o -> o.sendMessage(CC.translate(msg)));
            }
        }
    }

    @IncomingPacketHandler
    public void onReport(StaffMessages.ReportPacket packet) {
        String[] message = Holiday.getInstance().getMessages().getString("REPORTS.FORMAT")
                .replace("<server>", packet.getServer())
                .replace("<player>", packet.getReporter())
                .replace("<target>", packet.getReported())
                .replace("<reason>", packet.getReason())
                .split("\n");

        for (String s : message) {
            Holiday.getInstance().infoConsole(s);
            Clickable clickable = new Clickable(s,
                    Holiday.getInstance().getMessages().getString("REPORTS.CLICKMESSAGE")
                            .replace("<server>", packet.getServer()),
                    "/join " + packet.getServer()
            );

            Holiday.getInstance().getProfileHandler().getOnlineProfiles().stream()
                    .filter(Profile::isStaff)
                    .map(Profile::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(clickable::sendToPlayer);
        }
    }

    @IncomingPacketHandler
    public void onRequest(StaffMessages.HelpopPacket packet) {
        String[] message = Holiday.getInstance().getMessages().getString("HELPOPS.FORMAT")
                .replace("<server>", packet.getServer())
                .replace("<player>", packet.getSender())
                .replace("<reason>", packet.getRequest())
                .split("\n");
        for (String s : message) {
            Holiday.getInstance().infoConsole(s);

            Clickable clickable = new Clickable(s,
                    Holiday.getInstance().getMessages().getString("HELPOPS.CLICKMESSAGE")
                            .replace("<player>", packet.getSender()),
                    "/msg " + packet.getSender()
            );

            Holiday.getInstance().getProfileHandler().getOnlineProfiles().stream()
                    .filter(Profile::isStaff)
                    .map(Profile::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(clickable::sendToPlayer);
        }
    }


}
