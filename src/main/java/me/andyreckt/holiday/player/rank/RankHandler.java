package me.andyreckt.holiday.player.rank;

import com.mongodb.Block;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.player.punishments.PunishData;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.*;

public class RankHandler {

     Map<UUID, Rank> rankCache;

     public RankHandler() {
         init();
     }

     void init() {
         rankCache = new HashMap<>();
         MongoUtils.getRankCollection().find().forEach((Block<Document>) o -> {
             Rank rank = new Rank(o);
             rankCache.put(rank.getUuid(), rank);
         });
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
                .append("prefix", "&aDefault")
                .append("suffix", "")
                .append("bold", false)
                .append("italic", false)
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
        rankCache.put(rank.getUuid(), rank);
        return rank;
    }

    public Rank getDefaultRank() {
        for (Rank rank : rankCache.values()) {
            if (rank.isDefault()) return rank;
        }
        return createDefaultRank();
    }

    public Rank getHighestRank() {
         List<Rank> ran = new ArrayList<>(ranks());
         ran.sort(Comparator.comparingInt(Rank::getPriority));
         return ran.get(0);
    }
}
