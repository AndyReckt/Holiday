package me.andyreckt.holiday.core;

import lombok.Getter;
import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.IGrant;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.core.user.UserManager;
import me.andyreckt.holiday.core.user.grant.GrantManager;
import me.andyreckt.holiday.core.user.rank.Rank;
import me.andyreckt.holiday.core.user.rank.RankManager;
import me.andyreckt.holiday.core.util.mongo.MongoManager;
import me.andyreckt.holiday.core.util.mongo.MongoCredentials;
import me.andyreckt.holiday.core.util.redis.Midnight;
import me.andyreckt.holiday.core.util.redis.RedisCommand;
import me.andyreckt.holiday.core.util.redis.RedisCredentials;
import me.andyreckt.holiday.core.util.redis.pubsub.subscribers.GrantUpdateSubscriber;
import me.andyreckt.holiday.core.util.redis.pubsub.subscribers.ProfileUpdateSubscriber;
import me.andyreckt.holiday.core.util.redis.pubsub.subscribers.RankUpdateSubscriber;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class HolidayAPI implements API {

    private static HolidayAPI instance;

    private final Midnight midnight;
    private final MongoManager mongoManager;
    private final UserManager userManager;
    private final RankManager rankManager;
    private final GrantManager grantManager;
//    private final ServerManager serverManager;
//    private final PunishmentManager punishmentManager;



    public HolidayAPI(MongoCredentials mongoCredentials, RedisCredentials redisCredentials) {
        instance = this;

        this.mongoManager = new MongoManager(this, mongoCredentials);
        this.midnight = new Midnight(redisCredentials.getPool());
        this.userManager = new UserManager(this);
        this.rankManager = new RankManager(this);
        this.grantManager = new GrantManager(this);
        //TODO: ServerManager
        //this.serverManager = new ServerManager(this);
        //TODO: Punishments
        //this.punishmentManager = new PunishmentManager(this);

        this.loadRedis();
    }

    private void loadRedis() {
        Arrays.asList(
                new GrantUpdateSubscriber(),
                new ProfileUpdateSubscriber(),
                new RankUpdateSubscriber()
        ).forEach(midnight::registerListener);
        Arrays.asList(
                GrantUpdateSubscriber.class,
                ProfileUpdateSubscriber.class,
                RankUpdateSubscriber.class
        ).forEach(midnight::registerObject);
    }


    public static HolidayAPI getUnsafeAPI() {
        return instance;
    }

    @Override
    public Profile getProfile(UUID uuid) {
        return this.userManager.getProfiles().get(uuid);
    }

    @Override
    public HashMap<UUID, Profile> getProfiles() {
        return this.userManager.getProfiles();
    }

    @Override
    public void saveProfile(Profile profile) {
        this.userManager.saveProfile(profile);
    }

    @Override
    public IRank getDefaultRank() {
        return this.rankManager.getDefaultRank();
    }

    @Override
    public List<IRank> getRanks() {
        return this.rankManager.getRanks();
    }

    @Override
    public IRank createRank(String name) {
        return new Rank(name);
    }

    @Override
    public void saveRank(IRank rank) {
        this.rankManager.saveRank(rank);
    }

    @Override
    public void deleteRank(IRank rank) {
        this.rankManager.deleteRank(rank);
    }

    @Override
    public IRank getRank(String name) {
        return getRanks().stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public IRank getRank(UUID uuid) {
        return getRanks().stream().filter(rank -> rank.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    @Override
    public List<IGrant> getGrants() {
        return this.grantManager.getGrants();
    }

    @Override
    public List<IGrant> getGrants(UUID uuid) {
        return this.grantManager.getGrants().stream().filter(grant -> grant.getUser().equals(uuid)).collect(Collectors.toList());
    }

    @Override
    public IGrant getGrantFromId(UUID grantId) {
        return this.grantManager.getGrants().stream().filter(grant -> grant.getGrantId().equals(grantId)).findFirst().orElse(null);
    }

    @Override
    public void refreshGrants() {
        this.grantManager.updateGrants();
    }

    @Override
    public void saveGrant(IGrant grant) {
        this.grantManager.saveGrant(grant);
    }

    @Override
    public void removeGrant(IGrant grant) {
        this.grantManager.deleteGrant(grant);
    }

    @Override
    public <T> T runRedisCommand(RedisCommand<T> redisCommand) {
        Jedis jedis = this.midnight.getPool().getResource();
        T result = null;
        try {
            result = redisCommand.execute(jedis);
        }
        catch (Exception e) {
            e.printStackTrace();
            if (jedis != null) {
                this.midnight.getPool().returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally {
            if (jedis != null) {
                this.midnight.getPool().returnResource(jedis);
            }
        }
        return result;
    }
}
