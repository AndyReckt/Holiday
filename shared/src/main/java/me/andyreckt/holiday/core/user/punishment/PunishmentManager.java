package me.andyreckt.holiday.core.user.punishment;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.PunishmentUpdatePacket;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
        List<IPunishment> punishments = new ArrayList<>(this.punishments);
        punishments.sort((o1, o2) -> {
            if (o1.isActive() && !o2.isActive()) {
                return -1;
            } else if (!o1.isActive() && o2.isActive()) {
                return 1;
            }
            if (o1.getAddedAt() > o2.getAddedAt()) {
                return 1;
            } else if (o1.getAddedAt() < o2.getAddedAt()) {
                return -1;
            }
            return 0;
        });
        this.punishments.clear();
        this.punishments.addAll(punishments);
    }

    private IPunishment loadPunishment(Document document) {
        return GsonProvider.GSON.fromJson(document.toJson(), Punishment.class);
    }


    public void savePunishment(IPunishment punishment) {
        this.punishments.removeIf(pun -> pun.getId().equals(punishment.getId()));
        this.punishments.add(punishment);

        CompletableFuture.runAsync(() -> {
            api.getMongoManager().getPunishments().replaceOne(
                    Filters.eq("_id", punishment.getId()),
                    Document.parse(GsonProvider.GSON.toJson((Punishment) punishment)),
                    new ReplaceOptions().upsert(true)
            );
        });

        PacketHandler.send(new PunishmentUpdatePacket((Punishment) punishment));
    }

    public void refreshPunishments() {
        for (IPunishment o : getPunishments()) {
            if (o.check()) savePunishment(o);
        }
    }

    public void revokePunishment(IPunishment punishment, UUID revokedBy, String revokedReason, String revokedOn) {
        punishment.revoke(revokedBy, revokedReason, revokedOn);
        savePunishment(punishment);
    }
}
