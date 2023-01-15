package me.andyreckt.holiday.bukkit.server.redis.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.Logger;
import me.andyreckt.holiday.bukkit.util.other.Tasks;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@RequiredArgsConstructor
public class CrossServerCommandPacket implements Packet {

    private final String command;
    private final String server;

    @Override
    public void onReceive() {
        if (server.equalsIgnoreCase(Holiday.getInstance().getThisServer().getServerId()) || server.equalsIgnoreCase("ALL")) {
            Logger.log("&dRunning command '" + command + "'...");
            String cmd = command;
            final String _cmd = cmd.startsWith("/") ? cmd.substring(1) : cmd;
            Tasks.run(() -> Holiday.getInstance().getServer().dispatchCommand(Holiday.getInstance().getServer().getConsoleSender(), _cmd));
        }
    }
}

