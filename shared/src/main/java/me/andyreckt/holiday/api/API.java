package me.andyreckt.holiday.api;

import me.andyreckt.holiday.api.global.*;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.mongo.MongoCredentials;
import me.andyreckt.holiday.core.util.redis.Messaging;
import me.andyreckt.holiday.core.util.redis.RedisCredentials;

public interface API extends ProfileAPI, RankAPI, GrantAPI, PunishmentAPI, ServerAPI {

    Messaging getRedis();

    static API create(MongoCredentials mongoCredentials, RedisCredentials redisCredentials) {
        return new HolidayAPI(mongoCredentials, redisCredentials);
    }

}
