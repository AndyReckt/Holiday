package me.andyreckt.holiday.player;

import com.mongodb.client.model.Filters;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.player.disguise.impl.v1_7.DisguiseHandler_1_7;
import me.andyreckt.holiday.player.disguise.impl.v1_8.DisguiseHandler_1_8;
import me.andyreckt.holiday.server.nms.impl.NMS_v1_8;
import me.andyreckt.holiday.utils.Tasks;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class ProfileHandler {

    private final Map<UUID, Profile> profileCache;

    public ProfileHandler() {
        this.profileCache = new HashMap<>();
    }

    public List<Profile> getOnlineProfiles() {
        List<Profile> profiles = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(p -> profiles.add(getByUUID(p.getUniqueId())));
        return profiles;
    }

    public Profile getByUUIDFor5Minutes(UUID uuid) {
        if (uuid == getConsoleProfile().getUuid()) return getConsoleProfile();
        if (profileCache.containsKey(uuid)) return profileCache.get(uuid);
        return new Profile(uuid);
    }

    public Profile getByUUID(UUID uuid) {
        return getByUUID(uuid, true);
    }

    public Profile getByUUID(UUID uuid, boolean cache) {
        if (uuid == getConsoleProfile().getUuid()) return getConsoleProfile();
        if (profileCache.containsKey(uuid)) return profileCache.get(uuid);
        return new Profile(uuid, cache);
    }

    public Profile getByCommandSender(CommandSender sender) {
        if (sender instanceof Player) return getByPlayer(((Player) sender));
        else return getConsoleProfile();
    }

    public Profile getByPlayer(Player player) {
        return getByUUID(player.getUniqueId());
    }

    public Profile getByName(String name) {
        if (Holiday.getInstance().getNmsHandler() instanceof NMS_v1_8) {
            if (DisguiseHandler_1_8.DisguiseRequest.alreadyUsed(name)) {
                return Holiday.getInstance().getProfileHandler().getByUUIDFor5Minutes(DisguiseHandler_1_8.DisguiseRequest.getDataFromName(name).uuid());
            }
        } else {
            if (DisguiseHandler_1_7.DisguiseRequest.alreadyUsed(name)) {
                return Holiday.getInstance().getProfileHandler().getByUUIDFor5Minutes(DisguiseHandler_1_7.DisguiseRequest.getDataFromName(name).uuid());
            }
        }

        if (hasProfile(name)) {
            return new Profile(name);
        } else return null;

    }

    public boolean hasProfile(UUID uuid) {
        if (uuid == getConsoleProfile().getUuid()) return true;
        Document document = MongoUtils.getProfileCollection().find(Filters.eq("_id", uuid.toString())).first();
        return document != null;
    }

    public boolean hasProfile(String name) {
        if (Holiday.getInstance().getNmsHandler() instanceof NMS_v1_8) {
            if (DisguiseHandler_1_8.DisguiseRequest.alreadyUsed(name)) return true;
        } else {
            if (DisguiseHandler_1_7.DisguiseRequest.alreadyUsed(name)) return true;
        }

        if (this.cachedProfiles().stream().filter(profile -> profile.getLowerCaseName().equalsIgnoreCase(name)).findFirst().orElse(null) != null) return true;

        List<Document> docs = MongoUtils.getProfileCollection().find().into(new ArrayList<>());
        if (docs.stream().filter(doc -> doc.getString("name").equalsIgnoreCase(name)).findFirst().orElse(null) != null) return true;
        if (docs.stream().filter(doc -> doc.getString("lname").equalsIgnoreCase(name)).findFirst().orElse(null) != null) return true;

        Document document = MongoUtils.getProfileCollection().find(Filters.eq("lname", name.toLowerCase())).first();
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

    public List<Profile> cachedProfiles() {
        return new ArrayList<>(profileCache.values());
    }

}
