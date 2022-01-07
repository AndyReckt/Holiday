package me.andyreckt.holiday.player;

import com.mongodb.client.model.Filters;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.player.disguise.DisguiseHandler;
import me.andyreckt.holiday.utils.Tasks;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileHandler {

    Map<UUID, Profile> profileCache;

    public ProfileHandler() {
        this.profileCache = new ConcurrentHashMap<>();
    }

    public List<Profile> getOnlineProfiles() {
        List<Profile> profiles = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(p -> profiles.add(getByUUID(p.getUniqueId())));
        return profiles;
    }

    public Profile getByUUIDFor5Minutes(UUID uuid) {
        if (profileCache.containsKey(uuid)) return profileCache.get(uuid);
        Profile profile = new Profile(uuid);
        Tasks.runAsyncLater(() -> profileCache.remove(uuid), 5*60*20);
        return profile;
    }

    public Profile getByUUID(UUID uuid) {
        return getByUUID(uuid, true);
    }

    public Profile getByUUID(UUID uuid, boolean cache) {
        if(profileCache.containsKey(uuid)) return profileCache.get(uuid);
        return new Profile(uuid, cache);
    }

    public Profile getFromCommandSender(CommandSender sender) {
        if (sender instanceof Player) return getByUUID(((Player) sender).getUniqueId());
        else return getConsoleProfile();
    }

    public Profile getByPlayer(Player player) {
        return getByUUID(player.getUniqueId());
    }

    public Profile getFromNameFor5Minute(String name) {
        if(DisguiseHandler.DisguiseRequest.alreadyUsed(name)) {
            return Holiday.getInstance().getProfileHandler().getByUUIDFor5Minutes(DisguiseHandler.DisguiseRequest.getDataFromName(name).uuid());
        }
        Profile profile = new Profile(name.toLowerCase());
        Tasks.runAsyncLater(() -> profileCache.remove(profile.getUuid()), 5*60*20);
        return profile;
    }

    public boolean hasProfile(UUID uuid) {
        Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("_id", uuid.toString())).first();
        return document != null;
    }

    public boolean hasProfile(String name) {
        if(DisguiseHandler.DisguiseRequest.alreadyUsed(name)) return true;

        Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("lname", name.toLowerCase())).first();
        return document != null;
    }

    public Profile getConsoleProfile() {
        return new Profile();
    }

    public void updateProfile(Profile profile) {
        profileCache.put(profile.getUuid(), profile);
    }

    public boolean isCached(UUID uuid) {
        return profileCache.get(uuid) != null;
    }

    public void removeFromCache(UUID uuid) {
        profileCache.remove(uuid);
    }

}
