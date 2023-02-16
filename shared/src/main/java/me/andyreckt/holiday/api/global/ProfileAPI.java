package me.andyreckt.holiday.api.global;

import me.andyreckt.holiday.api.user.Profile;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ProfileAPI {

    Profile getProfile(UUID uuid);

    CompletableFuture<HashMap<UUID, Profile>> getAllProfiles();

    void saveProfile(Profile profile);

    void deleteProfile(Profile profile);
}

