package me.andyreckt.holiday.database.redis;

import lombok.Getter;
import me.andyreckt.holiday.database.redis.packets.*;
import me.andyreckt.holiday.database.redis.subscibers.BroadcastSubscriber;
import me.andyreckt.holiday.database.redis.subscibers.RankSubscriber;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import me.andyreckt.holiday.utils.packets.Pidgin;
import me.andyreckt.holiday.utils.packets.RedisCredentials;
import me.andyreckt.holiday.database.redis.subscibers.PunishmentSubscriber;
import me.andyreckt.holiday.database.redis.subscibers.ServerStartupSubscriber;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;

@Getter
public class Redis {

    RedisCredentials credentials;
    Pidgin pidgin;
    JedisPool jedis;

    public Redis(BasicConfigurationFile config) {

        credentials = new RedisCredentials(
                config.getString("REDIS.HOSTNAME"),
                config.getInteger("REDIS.PORT"),
                config.getBoolean("REDIS.AUTH"),
                config.getString("REDIS.PASSWORD"));


        if (credentials.isAuth()) {
            jedis = new JedisPool(new JedisPoolConfig(),
                    credentials.getHostname(),
                    credentials.getPort(),
                    2000,
                    credentials.getPassword());
        } else {
            jedis = new JedisPool(credentials.getHostname(), credentials.getPort());
        }

        pidgin = new Pidgin(config.getString("REDIS.CHANNEL"), jedis);
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
