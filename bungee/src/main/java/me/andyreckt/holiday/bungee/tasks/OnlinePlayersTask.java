package me.andyreckt.holiday.bungee.tasks;

import me.andyreckt.holiday.bungee.Bungee;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.OnlinePlayersPacket;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class OnlinePlayersTask {

    public OnlinePlayersTask() {
        Bungee.getInstance().getProxy().getScheduler().schedule(Bungee.getInstance(), () -> {

            HashMap<UUID, String> map = new HashMap<>();

            Bungee.getInstance().getProxy().getPlayers().forEach(player -> {
                if (player.getServer() != null) {
                    map.put(player.getUniqueId(), player.getServer().getInfo().getName());
                }
            });

            Bungee.getInstance().getApi().getMidnight().sendObject(new OnlinePlayersPacket(map));
        }, 0, 2, TimeUnit.SECONDS);
    }

}
