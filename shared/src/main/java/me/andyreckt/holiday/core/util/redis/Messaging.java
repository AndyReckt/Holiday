package me.andyreckt.holiday.core.util.redis;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.andyreckt.holiday.core.util.redis.messaging.IncomingPacketHandler;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;
import me.andyreckt.holiday.core.util.redis.messaging.PacketListener;
import lombok.Getter;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.config.Config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Getter
public class Messaging {

    private final RedissonClient client;
    private final RTopic topic;

    private final Gson gson = new GsonBuilder()
            .setLenient()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    private final HashMap<Class<? extends Packet>, PacketListener> adapters;
    private final HashMap<Class<? extends Packet>, String> types;
    private final HashMap<String, Class<? extends Packet>> cTypes;
    private final static String SEPARATOR = "--@--";

    public Messaging(RedisCredentials credentials) {
        Config config = new Config();
        if (credentials.isAuth()) {
            config.useSingleServer()
                    .setAddress("redis://" + credentials.getHostname() + ":" + credentials.getPort())
                    .setPassword(credentials.getPassword());
        } else {
            config.useSingleServer()
                    .setAddress("redis://" + credentials.getHostname() + ":" + credentials.getPort());
        }

        this.client = Redisson.create(config);
        this.topic = this.client.getTopic("Holiday");

        this.adapters = new HashMap<>();
        this.types = new HashMap<>();
        this.cTypes = new HashMap<>();
        this.topic.addListener(String.class, new MessagingListener());
    }

    public void registerAdapter(Class<? extends Packet> clazz, PacketListener listener) {
        this.adapters.put(clazz, listener);
        String id = clazz.getSimpleName().toLowerCase();
        this.types.put(clazz, id);
        this.cTypes.put(id, clazz);
    }

    public void sendPacket(Packet packet) {
        CompletableFuture.runAsync(() ->
                this.topic.publish(types.get(packet.getClass()) + SEPARATOR + gson.toJson(packet))
        );
    }

    private class MessagingListener implements MessageListener<String> {
        @Override
        public void onMessage(CharSequence charSequence, String s) {
            CompletableFuture.runAsync(() -> {
                try {
                    String id = s.split(SEPARATOR)[0];
                    Packet packet = gson.fromJson(s.split(SEPARATOR)[1], cTypes.get(id));

                    Class<? extends Packet> clazz = null;
                    for (Map.Entry<Class<? extends Packet>, String> entry : types.entrySet()) {
                        Class<? extends Packet> aClass = entry.getKey();
                        String s1 = entry.getValue();
                        if (s1.equalsIgnoreCase(id)) clazz = aClass;
                    }

                    PacketListener listener = adapters.get(clazz);

                    for (Method m : listener.getClass().getDeclaredMethods()) {
                        if (m.getDeclaredAnnotation(IncomingPacketHandler.class) != null) {
                            try {
                                m.invoke(listener, packet);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception ignored) {}
            });
        }
    }
}