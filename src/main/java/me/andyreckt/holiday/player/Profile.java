package me.andyreckt.holiday.player;

import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.Files;
import me.andyreckt.holiday.database.utils.MongoUtils;
import me.andyreckt.holiday.grant.Grant;
import me.andyreckt.holiday.rank.Rank;
import me.andyreckt.holiday.utils.Tasks;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter @Setter
public class Profile {

    public static Map<UUID, Profile> profileCache = new HashMap<>();

    UUID uuid;
    String name, ip, currentServer;
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
            load(true);
        } else {
            create(player);
        }
    }

    public Profile(UUID uuid) {
        if(hasProfile(uuid)) {
            this.uuid = uuid;
            load(true);
        } else {
            create(uuid);
        }
    }
    public Profile(UUID uuid, boolean cache) {
        if(hasProfile(uuid)) {
            this.uuid = uuid;
            load(cache);
        } else {
            create(uuid);
        }
    }

    public static Profile getFromUUIDWithoutCache(UUID uuid) {
        return new Profile(uuid, false);
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
        Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("_id", uuid.toString())).first();
        return document != null;
    }



    public void load(boolean cache) {
        getProfileFromDb(uuid);
        if(cache) profileCache.put(uuid, this);
    }

    private void getProfileFromDb(UUID uuid) {
        MongoUtils.submitToThread(() -> {
            Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("_id", uuid.toString())).first();

            this.name = document.getString("name");
            this.ip = document.getString("ip");

            this.ips = document.getList("ips", String.class);

            this.frozen = false;
            this.vanished = document.getBoolean("vanished");
            this.online = Bukkit.getPlayer(uuid) != null;

            this.currentServer = Files.Config.SERVER_NAME.getString();

            this.highestRank = Rank.getFromUUID(UUID.fromString(document.getString("highestRank")));
            this.currentVisibleRank = Rank.getFromUUID(UUID.fromString(document.getString("currentVisibleRank")));
            this.ranks = Rank.getFromStringList(document.getList("ranks", String.class));
            this.grants = Grant.getAllGrants(uuid);

            this.firstLogin = document.getDate("firstLogin");
            this.lastSeen = document.getDate("lastSeen");
            this.alts = getAlts(uuid);
        });
    }

    public void save() {
        MongoUtils.submitToThread(() -> MongoUtils.getProfileCollection().replaceOne(Filters.eq("_id", uuid.toString()), toBson(), new ReplaceOptions().upsert(true)));
    }

    public void create(Player player) {
        this.uuid = player.getUniqueId();
        if(hasProfile(uuid)) {
            load(true);
            return;
        }
        this.name = player.getName();
        this.firstLogin = new Date();
        this.lastSeen = new Date();
        this.highestRank = Rank.getDefaultRank();
        this.vanished = false;
        this.currentVisibleRank = Rank.getDefaultRank();
        this.currentServer = Files.Config.SERVER_NAME.getString();
        this.ranks = new ArrayList<>(Collections.singletonList(Rank.getDefaultRank()));
        Grant grant = new Grant(this.uuid, null, Rank.getDefaultRank(), -1);
        grant.save();
        this.grants = new ArrayList<>(Collections.singletonList(grant));
        this.alts = new ArrayList<>(getAltsOnAccountCreate(ip, uuid));

        save();
        profileCache.put(uuid, this);
        Tasks.runLater(() -> reloadProfile(player.getUniqueId()) , 60L);
    }

    public void create(UUID uuid) {
        this.uuid = uuid;
        if(hasProfile(uuid)) {
            load(true);
            return;
        }

        Player player = Bukkit.getPlayer(uuid);

        List<String> ipsL = new ArrayList<>();
        ipsL.add(player.getAddress().getAddress().getHostAddress());
        this.name = player.getName();
        this.ip = player.getAddress().getAddress().getHostAddress();
        this.ips = ipsL;
        this.firstLogin = new Date();
        this.lastSeen = new Date();
        this.vanished = false;
        this.highestRank = Rank.getDefaultRank();
        this.currentVisibleRank = Rank.getDefaultRank();
        this.currentServer = Files.Config.SERVER_NAME.getString();
        this.ranks = new ArrayList<>(Collections.singletonList(Rank.getDefaultRank()));
        Grant grant = new Grant(uuid, null, Rank.getDefaultRank(), -1);
        grant.save();
        this.grants = new ArrayList<>(Collections.singletonList(grant));
        this.alts = new ArrayList<>(getAltsOnAccountCreate(ip, uuid));
        save();
        profileCache.put(uuid, this);
        Tasks.runLater(() -> reloadProfile(uuid) , 60L);
    }

    public void reloadProfile(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("_id", player.getUniqueId())).first();
        if (profileCache.containsKey(player.getUniqueId())) profileCache.remove(player.getUniqueId());
        this.uuid = player.getUniqueId();

        if(document != null) {
            String ipp;
            if(document.getString("ip") == null) {
                ipp = player.getAddress().getAddress().getHostAddress();
            } else {
                ipp = document.getString("ip");
            }
            List<String> ipsL = new ArrayList<>();
            if(document.getList(ips, String.class).isEmpty()) {
                ipsL.add(player.getAddress().getAddress().getHostAddress());
            } else {
                ipsL.addAll(document.getList("ips", String.class));
            }
            this.name = player.getName();
            this.ip = ipp;
            this.ips = ipsL;
            this.firstLogin = document.getDate("firstLogin");
            this.lastSeen = document.getDate("lastSeen");
            this.vanished = document.getBoolean("vanished");
            this.highestRank = Rank.getFromUUID(UUID.fromString("highestRank"));
            this.highestRank = Rank.getFromUUID(UUID.fromString("highestRank"));
            this.currentVisibleRank = Rank.getFromUUID(UUID.fromString("currentVisibleRank"));
            this.ranks = Rank.getFromStringList(document.getList("ranks", String.class));
            this.grants = Grant.getAllGrants(uuid);
            this.alts = getAlts(player);

            save();
            profileCache.put(uuid, this);
        }

    }

    public Document toBson() {
        Profile profile = profileCache.get(uuid);
        List<String> rankList = new ArrayList<>();
        profile.getRanks().forEach(r -> {
            Rank rank = Rank.getFromUUID(r.getUuid());
            rankList.add(rank.getUuid().toString());
        });
        List<String> altList = new ArrayList<>();
        profile.getAlts().forEach(alt -> altList.add(alt.toString()));
        return new Document("_id", profile.getUuid().toString())
                .append("name", profile.getName())
                .append("ip", profile.getIp())
                .append("ips", profile.getIps())
                .append("firstLogin", profile.getFirstLogin())
                .append("vanished", profile.isVanished())
                .append("lastSeen", profile.getLastSeen())
                .append("currentServer", profile.getCurrentServer())
                .append("highestRank",profile.getHighestRank().getUuid().toString())
                .append("currentVisibleRank", profile.getCurrentVisibleRank().getUuid().toString())
                .append("ranks", rankList)
                .append("alts", altList);

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

    public static List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<>();

        Bukkit.getOnlinePlayers().forEach(player -> profiles.add(Profile.getFromPlayer(player)));

        return profiles;
    }

    public static List<UUID> getAlts(UUID uuid) {
        if(profileCache.containsKey(uuid)) return getAltsCached(uuid); else return getAltsUncached(uuid);
    }

    public static List<UUID> getAlts(Player player) {
        return getAlts(player.getUniqueId());
    }

    public static List<UUID> getAlts(Profile profile) {
        return getAlts(profile.getUuid());
    }

    public static List<UUID> getAltsCached(UUID uuid) {
        return profileCache.get(uuid).getAlts();
    }


    public static List<UUID> getAltsUncached(UUID uuid) {
        return getFromUUIDWithoutCache(uuid).getAlts();
    }

    public static List<UUID> getAltsOnAccountCreate(String ip, UUID uuid) {
        List<UUID> toReturn = new ArrayList<>();

        MongoUtils.getProfileCollection()
                .find(Filters.eq("ip", ip))
                .forEach((Block<Document>) doc -> toReturn.add(UUID.fromString(doc.getString("_id"))));

        toReturn.add(uuid);
        return toReturn;
    }




}
