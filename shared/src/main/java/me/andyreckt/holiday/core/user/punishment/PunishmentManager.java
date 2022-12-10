package me.andyreckt.holiday.core.user.punishment;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.PunishmentUpdatePacket;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class PunishmentManager {

    private final HolidayAPI api;

    private final List<IPunishment> punishments;

    public PunishmentManager(HolidayAPI api) {
        this.api = api;
        this.punishments = new ArrayList<>();

        this.loadPunishments();
    }

    private void loadPunishments() {
        for (Document document : api.getMongoManager().getPunishments().find()) {
            IPunishment punishment = loadPunishment(document);
            punishments.add(punishment);
        }
    }

    private IPunishment loadPunishment(Document document) {
        return GsonProvider.GSON.fromJson(document.getString("data"), Punishment.class);
    }


    public void savePunishment(IPunishment punishment) {
        this.punishments.removeIf(pun -> pun.getId().equals(punishment.getId()));
        this.punishments.add(punishment);

        api.getMongoManager().getPunishments().replaceOne(
                Filters.eq("_id", punishment.getId()),
                new Document("data", GsonProvider.GSON.toJson(punishment)).append("_id", punishment.getId()),
                new ReplaceOptions().upsert(true)
        );

        this.api.getMidnight().sendObject(new PunishmentUpdatePacket((Punishment) punishment));
    }

    public void refreshPunishments() {
        for (IPunishment o : getPunishments()) {
            if (o.check()) savePunishment(o);
        }
    }

    public void revokePunishment(IPunishment punishment, UUID revokedBy, String revokedReason) {
        punishment.revoke(revokedBy, revokedReason);
        savePunishment(punishment);
    }
}
