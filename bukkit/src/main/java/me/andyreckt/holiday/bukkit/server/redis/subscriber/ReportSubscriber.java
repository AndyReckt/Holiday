package me.andyreckt.holiday.bukkit.server.redis.subscriber;

import me.andyreckt.holiday.bukkit.server.redis.packet.ReportPacket;
import me.andyreckt.holiday.bukkit.util.Logger;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.text.Clickable;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import org.bukkit.Bukkit;

public class ReportSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onReport(ReportPacket packet) {
        String[] message = Locale.REPORT_FORMAT.getString()
                .replace("%server%", packet.getServer())
                .replace("%player%", packet.getReporter())
                .replace("%target%", packet.getReported())
                .replace("%reason%", packet.getReason())
                .split("\n");

        for (String s : message) {
            Logger.log(CC.translate(s));
            Clickable clickable = new Clickable(s,
                    Locale.REPORT_CLICK_MESSAGE.getString()
                            .replace("%server%", packet.getServer()),
                    "/join " + packet.getServer()
            );

            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.hasPermission(Perms.STAFF_VIEW_REPORTS.get())) {
                    clickable.sendToPlayer(player);
                }
            });
        }
    }
}
