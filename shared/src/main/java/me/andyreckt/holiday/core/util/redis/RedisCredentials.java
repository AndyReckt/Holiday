package me.andyreckt.holiday.core.util.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@AllArgsConstructor
@Getter
public class RedisCredentials {

    private final String hostname;
    private final int port;

    private final boolean auth;
    private final String password;


    public RedisCredentials(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.auth = false;
        this.password = "foobar";
    }

    public JedisPool getPool() {
        return auth ? new JedisPool(
                new JedisPoolConfig(), hostname, port, 2000, password
        ) : new JedisPool(hostname, port);
    }

}