package me.andyreckt.holiday.core;

import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.IGrant;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.core.server.Server;
import me.andyreckt.holiday.core.server.ServerManager;
import me.andyreckt.holiday.core.user.UserManager;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.user.grant.GrantManager;
import me.andyreckt.holiday.core.user.punishment.PunishmentManager;
import me.andyreckt.holiday.core.user.rank.Rank;
import me.andyreckt.holiday.core.user.rank.RankManager;
import me.andyreckt.holiday.core.util.mongo.MongoManager;
import me.andyreckt.holiday.core.util.mongo.MongoCredentials;
import me.andyreckt.holiday.core.util.redis.Midnight;
import me.andyreckt.holiday.core.util.redis.RedisCommand;
import me.andyreckt.holiday.core.util.redis.RedisCredentials;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.*;
import me.andyreckt.holiday.core.util.redis.pubsub.subscribers.*;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter
public class HolidayAPI implements API {

    private static HolidayAPI instance;

    private final Midnight midnight;
    private final MongoManager mongoManager;
    private final UserManager userManager;
    private final RankManager rankManager;
    private final GrantManager grantManager;
    private final ServerManager serverManager;
    private final PunishmentManager punishmentManager;

    @Setter
    private HashMap<UUID, String> onlinePlayers;



    public HolidayAPI(MongoCredentials mongoCredentials, RedisCredentials redisCredentials) {
        instance = this;
        new me.andyreckt.holiday.api.HolidayAPI();

        this.mongoManager = new MongoManager(this, mongoCredentials);
        this.midnight = new Midnight(redisCredentials.getPool());
        this.userManager = new UserManager(this);
        this.rankManager = new RankManager(this);
        this.grantManager = new GrantManager(this);
        this.serverManager = new ServerManager(this);
        this.punishmentManager = new PunishmentManager(this);

        this.onlinePlayers = new HashMap<>();

        this.loadRedis();
    }

    private void loadRedis() {
        Arrays.asList(
                new GrantUpdateSubscriber(), new ProfileUpdateSubscriber(),
                new RankUpdateSubscriber(), new PunishmentUpdateSubscriber(),
                new OnlinePlayersSubscriber()
        ).forEach(midnight::registerListener);
        Arrays.asList(
                GrantUpdatePacket.class, ProfileUpdatePacket.class,
                RankUpdatePacket.class, PunishmentUpdatePacket.class,
                BroadcastPacket.class, OnlinePlayersPacket.class
        ).forEach(midnight::registerObject);
    }


    public static HolidayAPI getUnsafeAPI() {
        return instance;
    }

    @Override
    public Profile getProfile(UUID uuid) {
        return this.userManager.getProfiles().computeIfAbsent(uuid, k -> new UserProfile(uuid));
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
        this.grantManager.refreshGrants();
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

    @Override
    public void revokePunishment(IPunishment punishment, UUID revokedBy, String revokedReason) {
        this.punishmentManager.revokePunishment(punishment, revokedBy, revokedReason);
    }

    @Override
    public List<IPunishment> getPunishments(UUID uniqueId) {
        Profile profile = this.getProfile(uniqueId);
        List<IPunishment> toReturn = new ArrayList<>();
        for (IPunishment punishment : this.punishmentManager.getPunishments()) {
            if (punishment.getPunished().equals(uniqueId)) {
                toReturn.add(punishment);
            }
            if (punishment.getIp().equalsIgnoreCase(profile.getIp()) && (punishment.getType() == IPunishment.PunishmentType.IP_BAN || punishment.getType() == IPunishment.PunishmentType.BLACKLIST)) {
                if (!toReturn.contains(punishment)) {
                    toReturn.add(punishment);
                }
            }
            if (punishment.getType() == IPunishment.PunishmentType.BLACKLIST) {
                if (profile.getIps().contains(punishment.getIp())) {
                    if (!toReturn.contains(punishment)) {
                        toReturn.add(punishment);
                    }
                }
            }
        }
        return toReturn;
    }

    @Override
    public List<IPunishment> getPunishments() {
        return this.punishmentManager.getPunishments();
    }

    @Override
    public void addPunishment(IPunishment punishment) {
        this.punishmentManager.savePunishment(punishment);
    }

    @Override
    public void refreshPunishments() {
        this.punishmentManager.refreshPunishments();
    }

    @Override
    public boolean isOnline(UUID playerId) {
        return this.onlinePlayers.containsKey(playerId);
    }

    @Override
    public CompletableFuture<HashMap<String, Server>> getServers() {
        return this.serverManager.getServers();
    }

    @Override
    public CompletableFuture<Server> getServer(String serverId) {
        return this.serverManager.getServer(serverId);
    }

    @Override
    public CompletableFuture<Server> getServer(UUID playerId) {
        return this.getServer(this.onlinePlayers.get(playerId));
    }
}
