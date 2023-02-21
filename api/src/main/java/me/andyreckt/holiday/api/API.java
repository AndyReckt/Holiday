package me.andyreckt.holiday.api;

import me.andyreckt.holiday.api.global.*;

public interface API extends ProfileAPI, RankAPI, GrantAPI, PunishmentAPI, ServerAPI {

    <T> T runRedisCommand(RedisCommand<T> command);

}
