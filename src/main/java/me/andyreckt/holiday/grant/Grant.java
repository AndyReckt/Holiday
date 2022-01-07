package me.andyreckt.holiday.grant;

import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.rank.Rank;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This Class is from Zowpy
 * All credits to him
 *
 * @author Zowpy
 */
@Getter @Setter
public class Grant {

     final UUID uuid;
     Rank rank;

     UUID user, issuer;
     boolean active;
     long duration, executedAt;

    public Grant(UUID uuid) {
        this.uuid = uuid;
    }

    public Grant(UUID user, UUID issuer, Rank rank, long duration) {
        this.uuid = UUID.randomUUID();
        this.user = user;
        this.issuer = issuer;
        this.rank = rank;
        this.duration = duration;
        this.active = true;
        this.executedAt = System.currentTimeMillis();
    }

    public Grant(Document document) {
        this.uuid = UUID.fromString(document.getString("_id"));
        this.user = UUID.fromString(document.getString("user"));
        this.issuer = document.getString("issuer").equalsIgnoreCase("Console") ? null : UUID.fromString(document.getString("issuer"));
        this.rank = Rank.getFromUUID(UUID.fromString(document.getString("rank")));
        this.active = document.getBoolean("active");
        this.duration = document.getLong("duration");
        this.executedAt = document.getLong("executedAt");
    }

    public int getPriority() {
        return rank == null ? 0 : rank.getPriority();
    }

    public boolean expired() {
        if(duration == -1) return false;
        return (executedAt + duration) <= System.currentTimeMillis();
    }

    public void save() {
        MongoUtils.submitToThread(() -> MongoUtils.getGrantCollection().replaceOne(Filters.eq("_id", uuid.toString()), toBson(), new ReplaceOptions().upsert(true)));
    }

    public Document toBson() {
        return new Document("_id", uuid.toString())
                .append("user", user.toString())
                .append("issuer", issuer == null ? "Console" : issuer.toString())
                .append("rank", rank == null ? "null" : rank.getUuid().toString())
                .append("active", active)
                .append("duration", duration)
                .append("executedAt", executedAt);
    }

    public static List<Grant> getAllGrants(UUID user) {
        List<Grant> toReturn = new ArrayList<>();
        MongoUtils.submitToThread(() -> MongoUtils.getGrantCollection()
                .find(Filters.eq("user", user.toString()))
                .forEach((Block<Document>) doc -> toReturn.add(new Grant(doc))));
        return toReturn;
    }

}