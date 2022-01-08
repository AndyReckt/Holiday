package me.andyreckt.holiday.database.redis.subscribers;

import me.andyreckt.holiday.database.redis.packet.StaffMessages;
import me.andyreckt.holiday.other.enums.StaffMessageType;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.Clickable;
import me.andyreckt.holiday.utils.packets.handler.IncomingPacketHandler;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.ChatColor;

import java.util.Objects;

public class StaffMessagesSubscriber implements PacketListener {
//TODO DO THIS
    /*
    @IncomingPacketHandler
    public void onMsg(StaffMessages.StaffMessagesPacket packet) {

        String msg = CC.translate(packet.getMessage());
        if (packet.getClickCmd() != null && !packet.getClickCmd().equalsIgnoreCase("")) {
            Clickable clickable = new Clickable(msg, packet.getHoverMsg(), packet.getClickCmd());
            if (packet.getChannel() == StaffMessageType.STAFF) {
                Profile.getAllProfiles().stream()
                        .filter(profile -> profile.getRank().isStaff())
                        .map(Profile::getPlayer)
                        .filter(Objects::nonNull)
                        .forEach(clickable::sendToPlayer);
            } else {
                Profile.getAllProfiles().stream()
                        .filter(profile -> profile.getRank().isAdmin())
                        .map(Profile::getPlayer)
                        .filter(Objects::nonNull)
                        .forEach(clickable::sendToPlayer);
            }
        } else {
            if (packet.getChannel() == StaffMessageType.STAFF) {
                Profile.getAllProfiles().stream()
                        .filter(profile -> profile.getRank().isStaff())
                        .map(Profile::getPlayer)
                        .filter(Objects::nonNull)
                        .forEach(o -> o.sendMessage(msg));
            } else {
                Profile.getAllProfiles().stream()
                        .filter(profile -> profile.getRank().isAdmin())
                        .map(Profile::getPlayer)
                        .filter(Objects::nonNull)
                        .forEach(o -> o.sendMessage(msg));

            }
        }
    }

    @IncomingPacketHandler
    public void onReport(StaffMessages.ReportPacket packet) {
        String message = "&9[REPORT] &d[" + packet.getServer() + "] "
                + packet.getReporter() + " &9reported " + packet.getReported()
                + "&9.";
        String reason = " &7» &9Reason: &3" + packet.getReason();

        Clickable clickable = new Clickable(message, "&dClick to join " + packet.getServer(), "/join " + packet.getServer());
        Clickable clickable1 = new Clickable(reason, "&dClick to join " + packet.getServer(), "/join " + packet.getServer());

        Profile.getAllProfiles().stream()
                .filter(profile -> profile.getRank().isStaff())
                .map(Profile::getPlayer)
                .filter(Objects::nonNull)
                .forEach(p -> {
                    clickable.sendToPlayer(p);
                    clickable1.sendToPlayer(p);
                });
    }

    @IncomingPacketHandler
    public void onRequest(StaffMessages.HelpopPacket packet) {
        String message = "&2[HELPOP] &d[" + packet.getServer() + "] "
                + packet.getSender() + " &2needs help.";
        String reason = " &7» &2Request: &a" + packet.getRequest();

        Clickable clickable = new Clickable(message, "&dClick to answer", ClickEvent.Action.SUGGEST_COMMAND, "/msg " + ChatColor.stripColor(packet.getSender()) + " ");
        Clickable clickable1 = new Clickable(reason, "&dClick to answer", ClickEvent.Action.SUGGEST_COMMAND, "/msg " + ChatColor.stripColor(packet.getSender()) + " ");

        Profile.getAllProfiles().stream()
                .filter(profile -> profile.getRank().isStaff())
                .map(Profile::getPlayer)
                .filter(Objects::nonNull)
                .forEach(p -> {
                    clickable.sendToPlayer(p);
                    clickable1.sendToPlayer(p);
                });
    }
*/

}
