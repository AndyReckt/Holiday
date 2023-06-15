package me.andyreckt.holiday.bukkit.server.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.text.Clickable;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;
import org.bukkit.Bukkit;

@Getter
@AllArgsConstructor
public class ReportPacket implements Packet {

    private String reporter;
    private String reported;
    private String reason;
    private String server;
    private String serverId;

    @Override
    public void onReceive() {
        String[] message = Locale.REPORT_FORMAT.getString()
                .replace("%server%", server)
                .replace("%player%", reporter)
                .replace("%target%", reported)
                .replace("%reason%", reason)
                .split("%newline%");

        for (String s : message) {
            Clickable clickable = new Clickable(s,
                    Locale.REPORT_CLICK_MESSAGE.getString()
                            .replace("%server%", server),
                    "/join " + serverId
            );

            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.hasPermission(Perms.STAFF_VIEW_REPORTS.get()))
                    .forEach(clickable::sendToPlayer);
        }
    }
}