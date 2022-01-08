package me.andyreckt.holiday.player.grant;

import com.mongodb.Block;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.TimeUtil;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

public class GrantHandler {

    Map<UUID, Grant> grantCache;

    public GrantHandler() {
        init();
    }

    void init() {
        grantCache = new HashMap<>();
        MongoUtils.getGrantCollection().find().forEach((Block<Document>) o -> {
            Grant grant = new Grant(o);
            grantCache.put(grant.getUuid(), grant);
        });
    }

    public List<Grant> getGrants(UUID user) {
        return grants().stream().filter(g -> g.getUser() == user).collect(Collectors.toList());
    }

    public List<Grant> grants() {
        return new ArrayList<>(grantCache.values());
    }

    public Grant newDefaultGrant(UUID user) {
        Grant grant = new Grant(user,
                Holiday.getInstance().getProfileHandler().getConsoleProfile().getUuid(),
                Holiday.getInstance().getRankHandler().getDefaultRank(),
                TimeUtil.PERMANENT);
        grant.save();
        return grant;
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
