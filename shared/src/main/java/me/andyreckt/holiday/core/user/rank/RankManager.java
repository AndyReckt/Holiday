package me.andyreckt.holiday.core.user.rank;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.core.HolidayAPI;
import lombok.Getter;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.RankUpdatePacket;
import org.bson.Document;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
public class RankManager {
    private final HolidayAPI api;

    private final Queue<IRank> ranks;

    public RankManager(HolidayAPI api) {
        this.api = api;
        this.ranks = new ConcurrentLinkedQueue<>();
        this.loadRanks();
        if (this.ranks.stream().noneMatch(IRank::isDefault)) {
            IRank rank = newDefaultRank();
            saveRank(rank);
        }
    }

    private void loadRanks() {
        for (Document document : api.getMongoManager().getRanks().find()) {
            this.ranks.add(loadRank(document));
        }
    }

    private Rank loadRank(Document document) {
        return GsonProvider.GSON.fromJson(document.getString("data"), Rank.class);
    }

    public void saveRank(IRank rank) {
        CompletableFuture.runAsync(() -> {
            api.getMongoManager().getRanks().replaceOne(
                    Filters.eq("_id", rank.getUuid()),
                    new Document("data", GsonProvider.GSON.toJson(rank)).append("_id", rank.getUuid()),
                    new ReplaceOptions().upsert(true)
            );
        });

        this.ranks.removeIf(rank1 -> rank1.getUuid().equals(rank.getUuid()));
        this.ranks.add(rank);

        this.api.getRedis().sendPacket(new RankUpdatePacket((Rank) rank));
    }

    public void deleteRank(IRank rank) {
        api.getMongoManager().getRanks().deleteOne(Filters.eq("_id", rank.getUuid()));

        api.getGrantManager().getGrants().stream()
                .filter(grant -> grant.getRank().getUuid().equals(rank.getUuid()))
                .forEach(grant -> api.getGrantManager().deleteGrant(grant));

        this.ranks.removeIf(rank1 -> rank1.getUuid().equals(rank.getUuid()));
        this.api.getRedis().sendPacket(new RankUpdatePacket((Rank) rank, true));
    }

    public IRank getDefaultRank() {
        return this.ranks.stream().filter(IRank::isDefault).findFirst().orElseGet(this::newDefaultRank);
    }

    private IRank newDefaultRank() {
        Rank defaultRank = new Rank("Default");
        defaultRank.setPrefix("&a");
        defaultRank.setDisplayName("&aDefault");
        defaultRank.setVisible(true);
        defaultRank.setDefault(true);
        return defaultRank;
    }
}
