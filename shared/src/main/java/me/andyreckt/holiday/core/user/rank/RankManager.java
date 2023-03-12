package me.andyreckt.holiday.core.user.rank;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.core.HolidayAPI;
import lombok.Getter;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.RankUpdatePacket;
import org.bson.Document;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

@Getter
public class RankManager {
    private final HolidayAPI api;

    private final Set<IRank> ranks;

    public RankManager(HolidayAPI api) {
        this.api = api;
        this.ranks = new ConcurrentSkipListSet<>();
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
        return GsonProvider.GSON.fromJson(document.toJson(), Rank.class);
    }

    public void saveRank(IRank rank) {
        CompletableFuture.runAsync(() -> {
            api.getMongoManager().getRanks().replaceOne(
                    Filters.eq("_id", rank.getUuid().toString()),
                    Document.parse(GsonProvider.GSON.toJson((Rank) rank)),
                    new ReplaceOptions().upsert(true)
            );
        });

        this.ranks.removeIf(rank1 -> rank1.getUuid().toString().equals(rank.getUuid().toString()));
        this.ranks.add(rank);

        PacketHandler.send(new RankUpdatePacket((Rank) rank));
    }

    public void deleteRank(IRank rank) {
        api.getMongoManager().getRanks().deleteOne(Filters.eq("_id", rank.getUuid().toString()));

        api.getGrantManager().getGrants().stream()
                .filter(grant -> grant.getRank().getUuid().equals(rank.getUuid()))
                .forEach(grant -> api.getGrantManager().deleteGrant(grant));

        this.ranks.removeIf(rank1 -> rank1.getUuid().toString().equals(rank.getUuid().toString()));
        PacketHandler.send(new RankUpdatePacket((Rank) rank, true));
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
