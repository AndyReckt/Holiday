package me.andyreckt.holiday.core.util.redis.messaging;

import lombok.experimental.UtilityClass;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import redis.clients.jedis.Jedis;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@UtilityClass
public final class PacketHandler {
    public static final HolidayAPI api = HolidayAPI.getUnsafeAPI();

    public static void init() {
        connectToServer();
    }

    public static void connectToServer() {
        new Thread(() -> {
            try (Jedis jedis = api.getJedis().getResource()) {
                if (api.getRedisCredentials().isAuth()) {
                    jedis.auth(api.getRedisCredentials().getPassword());
                }

                PacketPubSub pubSub = new PacketPubSub();
                String channel = "Packet:All";
                jedis.subscribe(pubSub, channel);
            }
        }, "Holiday - Packet Subscribe Thread").start();
    }

    public static void send(Packet packet) {
        CompletableFuture.runAsync(() -> api.runRedisCommand((jedis) -> {
            String encodedPacket = packet.getClass().getName() + "||" + GsonProvider.GSON.toJson(packet);
            return jedis.publish("Packet:All", encodedPacket);
        }), api.getExecutor());
    }

    public static void sendNoCheck(Packet packet) {
        try (Jedis jedis = api.getJedis().getResource()) {
            if (api.getRedisCredentials().isAuth()) {
                jedis.auth(api.getRedisCredentials().getPassword());
            }
            String encodedPacket = packet.getClass().getName() + "||" + GsonProvider.GSON.toJson(packet);
            jedis.publish("Packet:All", encodedPacket);
        }
    }
}
