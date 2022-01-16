package me.andyreckt.holiday.player.rank;

import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.database.redis.packet.RankPacket;
import me.andyreckt.holiday.other.enums.RankType;
import me.andyreckt.holiday.player.punishments.PunishData;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

public class RankHandler {

    private Map<UUID, Rank> rankCache;

    public RankHandler() {
         init();
    }

    private void init() {
         rankCache = new HashMap<>();
         MongoUtils.getRankCollection().find().forEach((Block<Document>) o -> {
             Rank rank = new Rank(o);
             updateCache(rank.getUuid(), rank);
         });
         getDefaultRank();
    }

    public Rank createRank(String name) {
        return new Rank(UUID.randomUUID(), name, "&f", "&f", name, false, false, false, false, false, false, true, ChatColor.WHITE, 0, new ArrayList<>(), new ArrayList<>());
    }

    public boolean cacheContains(UUID id) {
        return rankCache.containsKey(id);
    }

    public void updateCache(UUID id, Rank rank) {
        rankCache.put(id, rank);
    }

    public void clearCache() {
        rankCache.clear();
    }

    public void removeFromCache(UUID id) {
         rankCache.remove(id);
    }


    public List<Rank> ranks() {
        return new ArrayList<>(rankCache.values());
    }

    public List<Rank> ranksSorted() {
        List<Rank> list = new ArrayList<>(ranks()).stream().sorted((Comparator.comparingInt(Rank::getPriority))).collect(Collectors.toList());
        Collections.reverse(list);
        return list;
    }

    public Rank getFromId(UUID id) {
        if (cacheContains(id)) return rankCache.get(id);
        else return null;
    }

    public Rank getFromName(String name) {
         if (ranks().stream().anyMatch(rank -> rank.getName().equalsIgnoreCase(name))) return ranks().stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findFirst().get();
         else return null;
    }

    public Rank createDefaultRank() {
        Document document = new Document("_id", UUID.randomUUID().toString())
                .append("name", "Default")
                .append("prefix", "&a")
                .append("suffix", "")
                .append("bold", false)
                .append("italic", false)
                .append("displayName", "&aDefault")
                .append("default", true)
                .append("staff", false)
                .append("admin", false)
                .append("dev", false)
                .append("visible", true)
                .append("priority", 1)
                .append("color", ChatColor.GREEN.name())
                .append("childs", new ArrayList<String>())
                .append("permissions", new ArrayList<String>());
        Rank rank = new Rank(document);
        rank.save();
        updateCache(rank.getUuid(), rank);
        return rank;
    }

    public Rank getDefaultRank() {
        for (Rank rank : ranks()) {
            if (rank.isDefault()) return rank;
        }
        return createDefaultRank();
    }

    public Rank getHighestRank() {
         List<Rank> ran = new ArrayList<>(ranks());
         ran.sort(Comparator.comparingInt(Rank::getPriority));
         Collections.reverse(ran);
         return ran.get(0);
    }

    public void deleteRank(Rank rank) {
        MongoUtils.getRankCollection().deleteOne(Filters.eq("_id", rank.getUuid().toString()));
        Holiday.getInstance().getRedis().sendPacket(new RankPacket(rank, RankType.DELETE));
    }
}
