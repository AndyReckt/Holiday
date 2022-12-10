package me.andyreckt.holiday.api;

import me.andyreckt.holiday.api.global.GrantAPI;
import me.andyreckt.holiday.api.global.ProfileAPI;
import me.andyreckt.holiday.api.global.PunishmentAPI;
import me.andyreckt.holiday.api.global.RankAPI;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.mongo.MongoCredentials;
import me.andyreckt.holiday.core.util.redis.Midnight;
import me.andyreckt.holiday.core.util.redis.RedisCommand;
import me.andyreckt.holiday.core.util.redis.RedisCredentials;

public interface API extends ProfileAPI, RankAPI, GrantAPI, PunishmentAPI {

    <T> T runRedisCommand(RedisCommand<T> command);

    Midnight getMidnight();

    static API create(MongoCredentials mongoCredentials, RedisCredentials redisCredentials) {
        return new HolidayAPI(mongoCredentials, redisCredentials);
    }

}
