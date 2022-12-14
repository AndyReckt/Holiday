package me.andyreckt.holiday.bukkit.server.redis.subscriber;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.CrossServerCommandPacket;
import me.andyreckt.holiday.bukkit.util.Logger;
import me.andyreckt.holiday.core.util.redis.annotations.RedisListener;

public class ServerSubscriber {

    @RedisListener
    public void onCrossServerCommand(CrossServerCommandPacket packet) {
        if (packet.getServer().equalsIgnoreCase(Holiday.getInstance().getThisServer().getServerId()) || packet.getServer().equalsIgnoreCase("ALL")) {
            Logger.log("&dRunning command '" + packet.getCommand() + "'...");
            String cmd = packet.getCommand();
            cmd = cmd.startsWith("/") ? cmd.substring(1) : cmd;
            Holiday.getInstance().getServer().dispatchCommand(Holiday.getInstance().getServer().getConsoleSender(), cmd);
        }
    }


}
