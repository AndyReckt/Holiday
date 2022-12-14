package me.andyreckt.holiday.core.util.redis;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import me.andyreckt.holiday.core.util.redis.annotations.RedisListener;
import me.andyreckt.holiday.core.util.redis.annotations.RedisObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Midnight {

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .setLenient()
            .setPrettyPrinting()
            .create();
    private static final String channel = "Midnight-Pool";
    private static final String splitRegex = "--;@;--";

    private final List<LData> dataList = new ArrayList<>();
    private final Map<String, Class<?>> objectMap = new HashMap<>();

    private final Executor executor;
    @Getter
    private final JedisPool pool;
    private JedisPubSub pubSub;


    public Midnight(JedisPool pool) {
        log("Initializing...");
        Instant instant = Instant.now();

        executor = Executors.newFixedThreadPool(2);

        this.pool = pool;
        setupPubSub(pool);

        log("Initialized in " + (Instant.now().toEpochMilli() - instant.toEpochMilli()) + "ms");
    }

    /**
     * Setup the JedisPubSub instance.
     *
     * @param pool The JedisPool instance.
     */
    @SneakyThrows
    private void setupPubSub(JedisPool pool) {
        if (this.pubSub != null) return;
        this.pubSub = new JedisPubSub() {
            @Override
            @SneakyThrows
            public void onMessage(String channel, String message) {
                if (!channel.equalsIgnoreCase(Midnight.channel)) return;

                String[] array = message.split(Midnight.splitRegex);
                String id = array[0];

                if (objectMap.get(id) == null) return;
                Object clazz = GSON.fromJson(array[1], objectMap.get(id));

                if (clazz == null) return;

                for (LData data : dataList) {
                    if (data.getClazz().getAnnotation(RedisObject.class) == null) continue;
                    if (data.getClazz().getAnnotation(RedisObject.class).id().equalsIgnoreCase(id)) {
                        data.getMethod().invoke(data.getObject(), clazz);
                    }
                }
            }
        };

        executor.execute(() -> {
            final Jedis jedis = pool.getResource();
            jedis.subscribe(pubSub, channel);
        });
    }

    /**
     * Sends an object through redis.
     *
     * @param object the instance of the class to send.
     */
    @SneakyThrows
    public void sendObject(Object object) {
        executor.execute(() -> {
            if (object.getClass().getAnnotation(RedisObject.class) == null) return;
            Jedis jedis = this.pool.getResource();
            String toSend = GSON.toJson(object);
            jedis.publish(channel, object.getClass().getAnnotation(RedisObject.class).id() + splitRegex + toSend);
        });
    }

    /**
     * Scan a class and registers it as an RedisObject if possible,
     * if not then checks if any method in the class can be registered as a RedisListener
     *
     * @param clazz The class to scan/register.
     */
    @Deprecated
    public void registerClass(Class<?> clazz) {
        log("Using Midnight#registerClass() is deprecated, please refrain to use it.");

        if (clazz.getAnnotation(RedisObject.class) != null) {
            this.objectMap.put(clazz.getAnnotation(RedisObject.class).id(), clazz);
            log("Registered class " + clazz.getSimpleName() + " as an Object");
            return;
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(RedisListener.class) != null) {
                if (!Modifier.isStatic(method.getModifiers())) continue;
                registerMethod(method);
            }
        }
    }

    /**
     * Scans a class and registers it as an RedisObject if possible
     *
     * @param clazz The class to register.
     */
    public void registerObject(Class<?> clazz) {
        if (clazz.getAnnotation(RedisObject.class) == null) return;
        this.objectMap.put(clazz.getAnnotation(RedisObject.class).id(), clazz);
        log("Registered class " + clazz.getSimpleName() + " as an Object");
    }

    /**
     * Scan a class and checks if any method in the class can be registered as a RedisListener
     *
     * @param clazz The class to scan.
     */
    public void registerListener(Object clazz) {
        for (Method method : clazz.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(RedisListener.class) == null) continue;
            registerMethod(method, clazz);
        }
    }

    /**
     * Caches an object in redis like a {@link HashMap}
     *
     * @param id The id of the object.
     * @param value The object to cache.
     */
    public void cache(String id, Object value) {
        executor.execute(() -> {
            Jedis jedis = this.pool.getResource();
            jedis.set(id, GSON.toJson(value));
        });
    }

    /**
     * Caches an object in redis like {@link HashMap} & {@link #cache(String, Object)} but permit the caching of unique objects while using the same id.
     * <br>
     * ie: caching a user profile in "profile:userId"
     *
     * @param id The id or type of the object. (ie: profile)
     * @param uid The unique id of the object. (ie: userId)
     * @param value The object to cache.
     */
    @SneakyThrows
    public void cache(String id, String uid, Object value) {
        executor.execute(() -> {
            Jedis jedis = this.pool.getResource();
            jedis.set(id + ":::" + uid, GSON.toJson(value));
        });
    }

    /**
     * Retrieves an object from redis like {@link HashMap#get(Object)}
     *
     * @param id the id of the object.
     * @param clazz the class/type of the object.
     * @return the object, or null if not found.
     */
    @SneakyThrows
    public <T> T get(String id, Class<T> clazz) {
        Jedis jedis = this.pool.getResource();
        String json = jedis.get(id);
        if (json == null) return null;
        return GSON.fromJson(json, clazz);
    }

    /**
     * Retrieves an object from redis like {@link HashMap#get(Object)}
     *
     * @param id the id of the object.
     * @param clazz the class/type of the object.
     * @return the object, or null if not found.
     */
    @SneakyThrows
    public <T> CompletableFuture<T> getAsync(String id, Class<T> clazz) {
        return CompletableFuture.supplyAsync(() -> {
            Jedis jedis = this.pool.getResource();
            String json = jedis.get(id);
            if (json == null) return null;
            return GSON.fromJson(json, clazz);
        }, executor);
    }

    /**
     * Retrieves an object from redis like {@link HashMap#get(Object)} & {@link #get(String, Class)} but retrieve an object which uses a unique id.
     *
     * @param id the id or type of the object. (ie: profile)
     * @param uid the unique id of the object. (ie: userId)
     * @param clazz the class/type of the object.
     * @return the object, or null if not found.
     */
    @SneakyThrows
    public <T> T get(String id, String uid, Class<T> clazz) {
        Jedis jedis = this.pool.getResource();
        String json = jedis.get(id + ":::" + uid);
        if (json == null) return null;
        return GSON.fromJson(json, clazz);
    }

    /**
     * Retrieves an object asynchronously from redis like {@link HashMap#get(Object)} & {@link #get(String, Class)} but retrieve an object which uses a unique id.
     *
     * @param id the id or type of the object. (ie: profile)
     * @param uid the unique id of the object. (ie: userId)
     * @param clazz the class/type of the object.
     * @return the object, or null if not found.
     */
    @SneakyThrows
    public <T> CompletableFuture<T> getAsync(String id, String uid, Class<T> clazz) {
        return CompletableFuture.supplyAsync(() -> {
            Jedis jedis = this.pool.getResource();
            String json = jedis.get(id + ":::" + uid);
            if (json == null) return null;
            return GSON.fromJson(json, clazz);
        }, executor);
    }

    /**
     * Retrieves all unique ids and objects associated with a specific id.
     *
     * @param id the id or type of the object. (ie: profile)
     * @param clazz the class/type of the object.
     * @return a map of unique ids and objects.
     */
    @SneakyThrows
    public <T> HashMap<String, T> getAll(String id, Class<T> clazz) {
        Jedis jedis = this.pool.getResource();
        HashMap<String, T> map = new HashMap<>();
        jedis.keys(id + ":::*").forEach(key -> {
            String json = jedis.get(key);
            if (json == null) return;
            map.put(key.replace(id + ":::", ""), GSON.fromJson(json, clazz));
        });
        return map;
    }

    /**
     * Retrieves all unique ids and objects associated with a specific id asynchronously.
     *
     * @param id the id or type of the object. (ie: profile)
     * @param clazz the class/type of the object.
     * @return a map of unique ids and objects.
     */
    @SneakyThrows
    public <T> CompletableFuture<HashMap<String, T>> getAllAsync(String id, Class<T> clazz) {
        return CompletableFuture.supplyAsync(() -> {
            Jedis jedis = this.pool.getResource();
            HashMap<String, T> map = new HashMap<>();
            jedis.keys(id + ":::*").forEach(key -> {
                String json = jedis.get(key);
                if (json == null) return;
                map.put(key.replace(id + ":::", ""), GSON.fromJson(json, clazz));
            });
            return map;
        }, executor);
    }

    /**
     * Removes an object from redis like {@link HashMap#remove(Object)}
     * @param id the id of the object.
     */
    public void remove(String id) {
        executor.execute(() -> {
            Jedis jedis = this.pool.getResource();
            jedis.del(id);
        });
    }

    /**
     * Removes an object from redis like {@link HashMap#remove(Object)} & {@link #remove(String)} but removes an object which uses a unique id.
     * @param id the id or type of the object. (ie: profile)
     * @param uid the unique id of the object. (ie: userId)
     */
    public void remove(String id, UUID uid) {
        executor.execute(() -> {
            Jedis jedis = this.pool.getResource();
            jedis.del(id + ":::" + uid);
        });
    }

    /**
     * Removes an all objects connected to an id from redis.
     * @param id the id or type of the object. (ie: profile)
     */
    public void removeAll(String id) {
        executor.execute(() -> {
            Jedis jedis = this.pool.getResource();
            jedis.keys(id + ":::*").forEach(jedis::del);
        });
    }



    /**
     * Registers a method as a RedisListener.
     *
     * @param method The method to register
     */
    @SneakyThrows
    private void registerMethod(Method method) {
        if (method.getParameterTypes().length != 1) throw new Exception("The amount of parameters a RedisListener method should have is one and only one.");
        if (!Modifier.isStatic(method.getModifiers())) throw new Exception("In order to register a method as a listener without creating a new instance of its class, the method must be static.");
        Class<?> clazz = method.getParameterTypes()[0];

        this.dataList.add(new LData(null, method, clazz));

        log("Registered method " + method.getName() + " in class " + method.getDeclaringClass().getSimpleName() + " as a Listener");
    }

    /**
     * Registers a method as a RedisListener.
     *
     * @param method The method to register
     * @param instance The instance of the class
     */
    @SneakyThrows
    private void registerMethod(Method method, Object instance) {
        if (method.getParameterTypes().length != 1) throw new Exception("The amount of parameters a RedisListener method should have is one and only one.");
        Class<?> clazz = method.getParameterTypes()[0];

        this.dataList.add(new LData(instance, method, clazz));

        log("Registered method " + method.getName() + " in class " + method.getDeclaringClass().getSimpleName() + " as a Listener");
    }
    
    /**
     * Utility to log a message to the console.
     * 
     * @param message The message to log.
     */
    public void log(String message) {
//        System.out.println("[Midnight] >> " + message);
    }
}
