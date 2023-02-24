package me.andyreckt.holiday.bukkit.server.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.text.Clickable;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public class HelpopPacket implements Packet {

    private String sender;
    private String request;
    private String server;

    @Override
    public void onReceive() {
        String[] message = Locale.HELPOP_FORMAT.getString()
                .replace("%server%", server)
                .replace("%player%", sender)
                .replace("%message%", request)
                .split("%newline%");
        for (String s : message) {
            Clickable clickable = new Clickable(s,
                    Locale.HELPOP_CLICK_MESSAGE.getString()
                            .replace("%player%", sender),
                    ClickEvent.Action.SUGGEST_COMMAND,
                    "/msg " + ChatColor.stripColor(sender)
            );

            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.hasPermission(Perms.STAFF_VIEW_HELPOP.get())) {
                    clickable.sendToPlayer(player);
                }
            });
        }
    }
}