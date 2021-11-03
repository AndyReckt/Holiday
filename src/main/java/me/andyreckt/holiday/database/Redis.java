package me.andyreckt.holiday.database;

import lombok.Getter;
import me.andyreckt.holiday.Files;
import me.andyreckt.holiday.database.packets.*;
import me.andyreckt.holiday.database.subscibers.BroadcastSubscriber;
import me.andyreckt.holiday.database.subscibers.RankSubscriber;
import me.andyreckt.holiday.utils.packets.Pidgin;
import me.andyreckt.holiday.utils.packets.RedisCredentials;
import me.andyreckt.holiday.database.subscibers.PunishmentSubscriber;
import me.andyreckt.holiday.database.subscibers.ServerStartupSubscriber;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;

public class Redis {

    static RedisCredentials credentials;
    @Getter static Pidgin pidgin;
    final JedisPool jedis;

    public Redis() {

        credentials = new RedisCredentials(Files.Config.REDIS_HOSTNAME.getString(), Files.Config.REDIS_PORT.getInteger(), Files.Config.REDIS_AUTH.getValue(), Files.Config.REDIS_PASSWORD.getString()); //<<<<<<<<<<<"Holiday",
        if (credentials.isAuth()) {
            jedis = new JedisPool(new JedisPoolConfig(),
                    credentials.getHostname(),
                    credentials.getPort(),
                    2000,
                    credentials.getPassword());
        } else {
            jedis = new JedisPool(credentials.getHostname(), credentials.getPort());
        }

        pidgin = new Pidgin("Holiday", jedis);
        loadSubscribers();
        Arrays.asList(
                PunishmentPacket.class,
                ServerStartPacket.class,
                RankDeletePacket.class,
                RankCreatePacket.class,
                RankUpdatePacket.class,
                BroadcastPacket.class
        ).forEach(packet -> pidgin.registerPacket(packet));
    }

     void loadSubscribers() {

        pidgin.registerListener(new ServerStartupSubscriber());
        pidgin.registerListener(new PunishmentSubscriber());
        pidgin.registerListener(new RankSubscriber());
        pidgin.registerListener(new BroadcastSubscriber());

     }

}
