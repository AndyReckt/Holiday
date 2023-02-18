package me.andyreckt.holiday.core.util.redis.messaging;

import me.andyreckt.holiday.core.util.json.GsonProvider;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CompletableFuture;

public final class PacketPubSub extends JedisPubSub {
    public void onMessage(final String channel, final String message) {
        int packetMessageSplit = message.indexOf("||");
        String packetClassStr = message.substring(0, packetMessageSplit);
        String messageJson = message.substring(packetMessageSplit + "||".length());
        Class<?> packetClass;
        try {
            packetClass = Class.forName(packetClassStr);
        } catch (ClassNotFoundException ignored) {
            return;
        }
        Packet packet = (Packet) GsonProvider.GSON.fromJson(messageJson, packetClass);
        try {
            packet.onReceive();
        } catch (Exception ignored) {}

    }
}
