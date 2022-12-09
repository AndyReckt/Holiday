package me.andyreckt.holiday.core.util.redis;

import redis.clients.jedis.Jedis;

public interface RedisCommand<T> {

    T execute(Jedis var1);

}