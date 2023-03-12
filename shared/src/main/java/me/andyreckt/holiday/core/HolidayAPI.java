package me.andyreckt.holiday.core;

import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.global.RedisCommand;
import me.andyreckt.holiday.api.server.IServer;
import me.andyreckt.holiday.api.user.IGrant;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.core.server.Server;
import me.andyreckt.holiday.core.server.ServerManager;
import me.andyreckt.holiday.core.user.UserManager;
import me.andyreckt.holiday.core.user.grant.GrantManager;
import me.andyreckt.holiday.core.user.punishment.PunishmentManager;
import me.andyreckt.holiday.core.user.rank.Rank;
import me.andyreckt.holiday.core.user.rank.RankManager;
import me.andyreckt.holiday.core.util.mongo.MongoManager;
import me.andyreckt.holiday.core.util.mongo.MongoCredentials;
import me.andyreckt.holiday.core.util.redis.RedisCredentials;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class HolidayAPI implements API {

    private static HolidayAPI instance;

    private final MongoManager mongoManager;
    private final UserManager userManager;
    private final RankManager rankManager;
    private final GrantManager grantManager;
    private final ServerManager serverManager;
    private final PunishmentManager punishmentManager;

    private final JedisPool jedis;

    @Setter private HashMap<UUID, String> onlinePlayers;

    @Getter private final RedisCredentials redisCredentials;

    @Setter private Consumer<BroadcastPacket> broadcastConsumer;
    @Setter private Consumer<Server> serverUpdateConsumer;
    private final Executor executor;



    public HolidayAPI(MongoCredentials mongoCredentials, RedisCredentials redisCredentials) {
        instance = this;
        new me.andyreckt.holiday.api.HolidayAPI();
        this.redisCredentials = redisCredentials;

        this.mongoManager = new MongoManager(this, mongoCredentials);
        this.jedis = redisCredentials.isAuth() ?
                  new JedisPool(new JedisPoolConfig(), redisCredentials.getHostname(), redisCredentials.getPort(), 20_000, redisCredentials.getPassword())
                : new JedisPool(new JedisPoolConfig(), redisCredentials.getHostname(), redisCredentials.getPort(), 20_000);

        this.executor = Executors.newSingleThreadExecutor();
        PacketHandler.init();

        this.userManager = new UserManager(this);
        this.rankManager = new RankManager(this);
        this.grantManager = new GrantManager(this);
        this.serverManager = new ServerManager(this);
        this.punishmentManager = new PunishmentManager(this);

        this.onlinePlayers = new HashMap<>();
    }

    public static HolidayAPI getUnsafeAPI() {
        return instance;
    }

    @Override
    public Profile getProfile(UUID uuid) {
        return this.userManager.getProfile(uuid);
    }

    @Override
    public CompletableFuture<HashMap<UUID, Profile>> getAllProfiles() {
        return this.userManager.getAllProfilesDb();
    }

    @Override
    public void saveProfile(Profile profile) {
        this.userManager.saveProfile(profile);
    }

    @Override
    public void deleteProfile(Profile profile) {
        this.userManager.deleteProfile(profile);
    }

    @Override
    public IRank getDefaultRank() {
        return this.rankManager.getDefaultRank();
    }

    @Override
    public Set<IRank> getRanks() {
        return this.rankManager.getRanks();
    }

    @Override
    public List<IRank> getRanksSorted() {
        List<IRank> toReturn = this.getRanks().stream().sorted(Comparator.comparingInt(IRank::getPriority)).collect(Collectors.toList());
        Collections.reverse(toReturn);
        return toReturn;
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
    public void revokeGrant(IGrant grant, UUID revokedBy, String revokedOn, String revokedReason) {
        this.grantManager.revokeGrant(grant, revokedBy, revokedOn, revokedReason);
    }

    @Override
    public void revokePunishment(IPunishment punishment, UUID revokedBy, String revokedReason, String revokedOn) {
        this.punishmentManager.revokePunishment(punishment, revokedBy, revokedReason, revokedOn);
    }

    @Override
    public List<IPunishment> getPunishments(UUID uniqueId) {
        Profile profile = this.getProfile(uniqueId);
        Set<IPunishment> toReturn = new HashSet<>();
        for (IPunishment punishment : this.punishmentManager.getPunishments()) {
            if (punishment.getPunished().equals(uniqueId)) {
                toReturn.add(punishment);
            }

            if (punishment.getType() == IPunishment.PunishmentType.IP_BAN) {
                for (UUID alt : profile.getAlts()) {
                    if (punishment.getPunished().equals(alt)) {
                        toReturn.add(punishment);
                    }
                }
            }

            if (punishment.getType() == IPunishment.PunishmentType.BLACKLIST) {
                if (profile.getIps().contains(punishment.getIp())) {
                    toReturn.add(punishment);
                }
                for (UUID alt : profile.getAlts()) {
                    Profile altProfile = this.getProfile(alt);
                    if (altProfile.getIp().equals(punishment.getIp())) {
                        toReturn.add(punishment);
                    }
                }
            }
        }
        return new ArrayList<>(toReturn);
    }

    @Override
    public List<IPunishment> getPunishments() {
        return this.punishmentManager.getPunishments();
    }

    @Override
    public void savePunishment(IPunishment punishment) {
        this.punishmentManager.savePunishment(punishment);
    }

    @Override
    public void refreshPunishments() {
        this.punishmentManager.refreshPunishments();
    }

    @Override
    public IPunishment getPunishment(String id) {
        return this.punishmentManager.getPunishments().stream().filter(punishment -> punishment.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    @Override
    public boolean isOnline(UUID playerId) {
        return this.onlinePlayers.containsKey(playerId);
    }

    @Override
    public Map<String, IServer> getServers() {
        return this.serverManager.getServers();
    }

    @Override
    public IServer getServer(String serverId) {
        return this.serverManager.getServer(serverId);
    }

    @Override
    public IServer getServer(UUID playerId) {
        return this.getServer(this.onlinePlayers.get(playerId));
    }

    @Override
    public <T> T runRedisCommand(RedisCommand<T> redisCommand) {
        Jedis jedis = this.jedis.getResource();
        if (redisCredentials.isAuth()) {
            jedis.auth(redisCredentials.getPassword());
        }

        T result = null;
        try {
            result = redisCommand.execute(jedis);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            jedis.close();
        }
        return result;
    }

}
