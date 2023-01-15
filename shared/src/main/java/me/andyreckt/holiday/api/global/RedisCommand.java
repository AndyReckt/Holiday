package me.andyreckt.holiday.api.global;

import redis.clients.jedis.Jedis;

public interface RedisCommand<T> {

    T execute(Jedis redis);

}