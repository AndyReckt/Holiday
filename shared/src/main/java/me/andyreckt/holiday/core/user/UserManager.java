package me.andyreckt.holiday.core.user;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.ProfileDeletePacket;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.ProfileUpdatePacket;
import org.bson.Document;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class UserManager {
    private final HolidayAPI api;

    private final ConcurrentHashMap<UUID, Profile> profiles;

    public UserManager(HolidayAPI api) {
        this.api = api;
        this.profiles = new ConcurrentHashMap<>();
        Profile console = UserProfile.getConsoleProfile();
        this.profiles.put(console.getUuid(), console);
//        this.loadProfiles();
    }

    private void loadProfiles() {
        getAllProfilesDb().whenCompleteAsync((map, ignored) -> {
            this.profiles.putAll(map);
            Profile console = UserProfile.getConsoleProfile();
            this.profiles.put(console.getUuid(), console);
        });
    }

    public Profile loadProfile(Document document) {
        return GsonProvider.GSON.fromJson(document.toJson(), UserProfile.class);
    }

    public void saveProfile(Profile profile) {
        this.profiles.put(profile.getUuid(), profile);
        CompletableFuture.runAsync(() -> {
            api.getMongoManager().getProfiles().replaceOne(
                    Filters.eq("_id", profile.getUuid().toString()),
                    Document.parse(GsonProvider.GSON.toJson((UserProfile) profile)),
                    new ReplaceOptions().upsert(true)
            );
            PacketHandler.send(new ProfileUpdatePacket((UserProfile) profile));
        });
    }

    public Profile getProfileNoCreate(UUID uuid) {
        if (profiles.containsKey(uuid)) {
            return profiles.get(uuid);
        }

        Document document = api.getMongoManager().getProfiles().find(Filters.eq("_id", uuid.toString())).first();
        return document == null ? null : loadProfile(document);
    }

    public Profile getProfile(UUID uuid) {
        if (profiles.containsKey(uuid)) {
            return profiles.get(uuid);
        }

        Document document = api.getMongoManager().getProfiles().find(Filters.eq("_id", uuid.toString())).first();
        Profile profile = document == null ? new UserProfile(uuid) : loadProfile(document);
        this.saveProfile(profile);
        return profile;
    }

    public CompletableFuture<HashMap<UUID, Profile>> getAllProfilesDb() {
        return CompletableFuture.supplyAsync(() -> {
            HashMap<UUID, Profile> profiles = new HashMap<>();
            for (Document document : api.getMongoManager().getProfiles().find()) {
                profiles.put(UUID.fromString(document.getString("_id")), loadProfile(document));
            }
            return profiles;
        });
    }

    public void deleteProfile(Profile profile) {
        this.profiles.remove(profile.getUuid());
        CompletableFuture.runAsync(() -> {
            api.getMongoManager().getProfiles().deleteOne(Filters.eq("_id", profile.getUuid().toString()));
        });
        PacketHandler.send(new ProfileDeletePacket(profile.getUuid()));
    }
}
