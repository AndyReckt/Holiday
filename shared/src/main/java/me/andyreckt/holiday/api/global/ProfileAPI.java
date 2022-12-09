package me.andyreckt.holiday.api.global;

import me.andyreckt.holiday.api.user.Profile;

import java.util.HashMap;
import java.util.UUID;

public interface ProfileAPI {

    Profile getProfile(UUID uuid);

    HashMap<UUID, Profile> getProfiles();

    void saveProfile(Profile profile);
}

