package me.andyreckt.holiday.player;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.database.utils.MongoUtils;
import me.andyreckt.holiday.grant.Grant;
import me.andyreckt.holiday.rank.Rank;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter @Setter
public class Profile {

    public static Map<UUID, Profile> profileCache = new HashMap<>();

    UUID uuid;
    String name, ip;
    List<String> ips;

    boolean frozen, vanished, online;

    Date firstLogin, lastSeen;

    Rank highestRank, currentVisibleRank;
    List<Rank> ranks;
    List<Grant> grants;

    List<UUID> alts;



    public Profile(Player player) {
        if(hasProfile(player.getUniqueId())) {
            this.uuid = player.getUniqueId();
            load();
        } else {
            create(player);
        }
    }

    public Profile(UUID uuid) {
        if(hasProfile(uuid)) {
            this.uuid = uuid;
            load();
        } else {
            create(uuid);
        }
    }

    public static Profile getFromPlayer(Player player) {
        if(profileCache.containsKey(player.getUniqueId())) return profileCache.get(player.getUniqueId());
        return new Profile(player);
    }

    public static Profile getFromUUID(UUID uuid) {
        if(profileCache.containsKey(uuid)) return profileCache.get(uuid);
        return new Profile(uuid);
    }

    public static boolean hasProfile(UUID uuid) {
        Document document = (Document) MongoUtils.getRankCollection().find(Filters.eq("_id", uuid.toString())).first();
        return document != null;
    }

    public Player getPlayer() {
        AtomicBoolean connected = new AtomicBoolean(false);

        Bukkit.getOnlinePlayers().forEach(player -> {
            if(player.getUniqueId() == uuid) connected.set(true);
        });

        if(connected.get()) return Bukkit.getPlayer(uuid);
        else return (Player) Bukkit.getOfflinePlayer(uuid);
    }

    public String getNameWithColor() {
        return currentVisibleRank.getColor() + name;
    }

    public void load() {
        MongoUtils.submitToThread(() -> {
            Document document = (Document) MongoUtils.getRankCollection().find(Filters.eq("_id", uuid.toString())).first();

            this.name = document.getString("name");
            this.ip = document.getString("ip");

            this.ips = document.getList("ips", String.class);

            this.frozen = false;
            this.vanished = document.getBoolean("vanished");
            this.online = Bukkit.getPlayer(uuid) != null;

            this.highestRank = Rank.getFromUUID(UUID.fromString("highestRank"));
            this.currentVisibleRank = Rank.getFromUUID(UUID.fromString("currentVisibleRank"));
            this.ranks = Rank.getFromStringList(document.getList("ranks", String.class));

            this.firstLogin = document.getDate("firstLogin");
            this.lastSeen = document.getDate("lastSeen");

        });
        profileCache.put(uuid, this);
    }

    public void save() {
        MongoUtils.submitToThread(() -> MongoUtils.getRankCollection().replaceOne(Filters.eq("_id", uuid.toString()), toBson(), new ReplaceOptions().upsert(true)));
    }

    public void create(Player player) {
        this.uuid = player.getUniqueId();
        if(hasProfile(uuid)) {
            load();
            return;
        }
        List<String> ipsL = new ArrayList<>();
        ipsL.add(player.getAddress().getAddress().getHostAddress());
        name = player.getName();
        ip = player.getAddress().getAddress().getHostAddress();
        ips = ipsL;
        firstLogin = new Date();
        lastSeen = new Date();
        save();
        profileCache.put(uuid, this);
    }

    public void create(UUID uuid) {
        this.uuid = uuid;
        if(hasProfile(uuid)) {
            load();
            return;
        }

        Player player = Bukkit.getPlayer(uuid);

        List<String> ipsL = new ArrayList<>();
        ipsL.add(player.getAddress().getAddress().getHostAddress());
        name = player.getName();
        ip = player.getAddress().getAddress().getHostAddress();
        ips = ipsL;
        firstLogin = new Date();
        lastSeen = new Date();
        highestRank = Rank.getDefaultRank();
        currentVisibleRank = Rank.getDefaultRank();
        ranks = new ArrayList<>(Collections.singletonList(Rank.getDefaultRank()));


        save();
        profileCache.put(uuid, this);

    }

    public static List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<>();

        Bukkit.getOnlinePlayers().forEach(player -> profiles.add(Profile.getFromPlayer(player)));

        return profiles;
    }


    public Document toBson() {
        return new Document("_id", uuid.toString())
                .append("name", name)
                .append("ip", ip)
                .append("ips", ips)
                .append("firstLogin", firstLogin)
                .append("lastSeen", lastSeen);
    }

}
