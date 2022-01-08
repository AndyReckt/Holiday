package me.andyreckt.holiday.database.redis;

import lombok.Getter;
import me.andyreckt.holiday.database.redis.packet.*;
import me.andyreckt.holiday.database.redis.subscribers.*;
import me.andyreckt.holiday.player.grant.Grant;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import me.andyreckt.holiday.utils.packets.Pidgin;
import me.andyreckt.holiday.utils.packets.RedisCredentials;
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
                BroadcastPacket.class,
                MessagePacket.class,
                ProfilePacket.class,
                DisguisePacket.class,
                ClickablePacket.class,
                ServerPacket.class,
                CrossServerCommandPacket.class,
                StaffSwitchServer.StaffPacket.class,
                ProfilePacket.ProfileDeletePacket.class,
                ProfilePacket.ProfileMessagePacket.class,
                StaffMessages.ReportPacket.class,
                StaffMessages.StaffMessagesPacket.class,
                StaffMessages.HelpopPacket.class,
                RankPacket.class,
                GrantPacket.class
        ).forEach(packet -> pidgin.registerPacket(packet));
    }

     void loadSubscribers() {
         pidgin.registerListener(new PunishmentSubscriber());
         pidgin.registerListener(new BroadcastSubscriber());
         pidgin.registerListener(new MessageSubscriber());
         pidgin.registerListener(new ProfileSubscriber());
         pidgin.registerListener(new DisguiseSubscriber());
         pidgin.registerListener(new ServerSubscriber());
         pidgin.registerListener(new RankSubscriber());
         pidgin.registerListener(new StaffMessagesSubscriber());
         pidgin.registerListener(new GrantSubscriber());

     }

}
