package me.andyreckt.holiday.core.user.grant;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.andyreckt.holiday.api.user.IGrant;
import me.andyreckt.holiday.core.HolidayAPI;
import lombok.Getter;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.GrantUpdatePacket;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GrantManager { //TODO: at some point only load the active grants, and get the rest when needed.
    private final HolidayAPI api;

    private final List<IGrant> grants;

    public GrantManager(HolidayAPI api) {
        this.api = api;
        this.grants = new ArrayList<>();

        this.loadGrants();
    }

    private void loadGrants() {
        for (Document document : api.getMongoManager().getGrants().find()) {
            IGrant grant = loadGrant(document);
            grants.add(grant);
        }
    }

    private IGrant loadGrant(Document document) {
        return GsonProvider.GSON.fromJson(document.getString("data"), Grant.class);
    }

    public void saveGrant(IGrant grant) {
        this.grants.removeIf(grant1 -> grant1.getGrantId().equals(grant.getGrantId()));
        this.grants.add(grant);

        api.getMongoManager().getGrants().replaceOne(
                Filters.eq("_id", grant.getGrantId()),
                new Document("data", GsonProvider.GSON.toJson(grant)).append("_id", grant.getGrantId()),
                new ReplaceOptions().upsert(true)
        );

        this.api.getRedis().sendPacket(new GrantUpdatePacket((Grant) grant));
    }

    public void deleteGrant(IGrant grant) {
        this.grants.removeIf(grant1 -> grant1.getGrantId().equals(grant.getGrantId()));

        api.getMongoManager().getGrants().deleteOne(Filters.eq("_id", grant.getGrantId()));

        this.api.getRedis().sendPacket(new GrantUpdatePacket((Grant) grant, true));
    }

    public void refreshGrants() {
        for (IGrant o : getGrants()) {
            if (o.check()) saveGrant(o);
        }
    }
}
