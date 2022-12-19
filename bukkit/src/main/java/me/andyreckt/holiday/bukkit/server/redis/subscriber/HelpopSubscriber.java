package me.andyreckt.holiday.bukkit.server.redis.subscriber;

import me.andyreckt.holiday.bukkit.server.redis.packet.HelpopPacket;
import me.andyreckt.holiday.bukkit.util.Logger;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.text.Clickable;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;

public class HelpopSubscriber implements PacketListener {

    @IncomingPacketHandler
    public void onRequest(HelpopPacket packet) {
        String[] message = Locale.HELPOP_FORMAT.getString()
                .replace("%server%", packet.getServer())
                .replace("%player%", packet.getSender())
                .replace("%reason%", packet.getRequest())
                .split("\n");
        for (String s : message) {
            Clickable clickable = new Clickable(s,
                    Locale.HELPOP_CLICK_MESSAGE.getString()
                            .replace("<player>", packet.getSender()),
                    ClickEvent.Action.SUGGEST_COMMAND,
                    "/msg " + packet.getSender()
            );

            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.hasPermission(Perms.STAFF_VIEW_HELPOP.get())) {
                    clickable.sendToPlayer(player);
                }
            });
        }
    }
}
