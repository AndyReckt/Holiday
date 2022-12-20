package me.andyreckt.holiday.core.user;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.ProfileUpdatePacket;
import org.bson.Document;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class UserManager {
    private final HolidayAPI api;

    private final HashMap<UUID, Profile> profiles;

    public UserManager(HolidayAPI api) {
        this.api = api;
        this.profiles = new HashMap<>();

        this.loadProfiles();
    }

    private void loadProfiles() {
        for (Document document : api.getMongoManager().getProfiles().find()) {
            Profile profile = loadProfile(document);

            this.profiles.put(profile.getUuid(), profile);
        }
        Profile console = UserProfile.getConsoleProfile();
        this.profiles.put(console.getUuid(), console);
    }

    public Profile loadProfile(Document document) {
        return GsonProvider.GSON.fromJson(document.getString("data"), UserProfile.class);
    }

    public void saveProfile(Profile profile) {
        this.profiles.put(profile.getUuid(), profile);
        api.getMongoManager().getProfiles().replaceOne(
                Filters.eq("_id", profile.getUuid()),
                new Document("_id", profile.getUuid()).append("data", GsonProvider.GSON.toJson(profile)),
                new ReplaceOptions().upsert(true)
        );
        api.getRedis().sendPacket(new ProfileUpdatePacket((UserProfile) profile));
    }
}
