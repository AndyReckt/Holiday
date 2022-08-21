package me.andyreckt.holiday.player.grant;

import com.mongodb.Block;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.Tasks;
import me.andyreckt.holiday.utils.TimeUtil;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GrantHandler {

    public Map<UUID, Grant> grantCache = new ConcurrentHashMap<>();

    public GrantHandler() {
        init();
    }

    private void init() {
        MongoUtils.getGrantCollection().find().forEach((Block<Document>) o -> {
            Grant grant = Grant.fromJson(o.toJson());
            grantCache.put(grant.getUuid(), grant);
        });
    }

    public List<Grant> getGrants(UUID target) {
        List<Grant> list = new ArrayList<>();
        for (Grant o : grants()) {
            if (o.getTarget() == null || target == null) continue;
            if (o.getTarget().toString().equalsIgnoreCase(target.toString())) {
                list.add(o);
            }
        }
        return list;
    }

    public List<Grant> grants() {
        return new ArrayList<>(grantCache.values());
    }

    public Grant newDefaultGrant(UUID user) {
        Grant grant = new Grant();
        grant.setTarget(user);
        grant.setIssuedBy(Holiday.getInstance().getProfileHandler().getConsoleProfile().getUuid());
        grant.setIssuedOn(Holiday.getInstance().getServerHandler().getThisServer().getName());
        grant.setIssuedAt(System.currentTimeMillis());
        grant.setRankId(Holiday.getInstance().getRankHandler().getDefaultRank().getUuid().toString());
        grant.setReason("Default Rank");
        grant.save();
        return grant;
    }

    public void refreshGrants() {
        for (Grant o : grants()) {
            if (o.hasExpired()) {
                o.setActive(false);
                o.save();
            }
        }
    }

    public void deleteGrantsFromRank(Rank rank) {
        for (Grant grant : grants()) {
            if (grant.getRank().getUuid().toString().equalsIgnoreCase(rank.getUuid().toString())) {
                grant.delete();
            }
        }
    }

    public void removeFromCache(UUID id) {
        grantCache.remove(id);
    }

    public boolean cacheContains(UUID id) {
        return grantCache.containsKey(id);
    }

    public void updateCache(UUID id, Grant grant) {
        grantCache.put(id, grant);
    }

    public void clearCache() {
        grantCache.clear();
    }
}
