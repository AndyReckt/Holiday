package me.andyreckt.holiday.player;


import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.database.redis.packet.ProfilePacket;
import me.andyreckt.holiday.player.disguise.DisguiseHandler;
import me.andyreckt.holiday.player.grant.Grant;
import me.andyreckt.holiday.player.punishments.PunishData;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.PunishmentUtils;
import me.andyreckt.holiday.utils.StringUtils;
import me.andyreckt.holiday.utils.Tasks;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class Profile {

    UUID uuid;
    String name, ip, lowerCaseName;
    List<String> ips;

    boolean online, liked, inStaffMode, messagesEnabled;
    int coins;

    long firstLogin, lastSeen;

    Rank rank;
    long rankTime;

    @Nullable
    String currentServer;

    DisguiseHandler.DisguiseData disguiseData;


    public Profile(Player player) {
        new Profile(player.getUniqueId(), true);
    }

    public Profile(String name) {
        if (hasProfile(name.toLowerCase())) {
            getProfileFromDb(name.toLowerCase());
        }
    }

    public Profile() {
        this.uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        this.name = "Console";
        this.online = true;
        this.rank = Holiday.getInstance().getRankHandler().getHighestRank();
    }

    public Profile(UUID uuid) {
        new Profile(uuid, true);
    }

    public Profile(Document document) {
        loadFromDocument(document);
    }

    public Profile(UUID uuid, boolean cache) {
        if (hasProfile(uuid)) {
            this.uuid = uuid;
            load(cache);
        } else {
            create(uuid);
        }
    }


    boolean hasProfile(UUID uuid) {
        Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("_id", uuid.toString())).first();
        return document != null;
    }

    boolean hasProfile(String name) {
        if (DisguiseHandler.DisguiseRequest.alreadyUsed(name)) return true;

        Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("lname", name.toLowerCase())).first();
        return document != null;
    }

    public void load(boolean cache) {
        getProfileFromDb(uuid);
        if (cache) Holiday.getInstance().getProfileHandler().updateProfile(this);
    }



    void getProfileFromDb(UUID uuid) {
        Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("_id", uuid.toString())).first();
        assert document != null;
        loadFromDocument(document);
    }

    void getProfileFromDb(String name) {
        if (hasProfile(name)) {
            Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("lname", name.toLowerCase())).first();
            assert document != null;
            new Profile(document);
        } else Holiday.getInstance().getLogger().warning("Couldnt find " + name + "'s profile");
    }

    public void save() {
        MongoUtils.submitToThread(() -> MongoUtils.getProfileCollection().replaceOne(Filters.eq("_id", uuid.toString()), toBson(), new ReplaceOptions().upsert(true)));
        Holiday.getInstance().getRedis().sendPacket(new ProfilePacket(this));
    }

    public void autoSave() {
        MongoUtils.submitToThread(() -> MongoUtils.getProfileCollection().replaceOne(Filters.eq("_id", uuid.toString()), toBson(), new ReplaceOptions().upsert(true)));
    }

    public void saveOnStop() {
        MongoUtils.getProfileCollection().replaceOne(Filters.eq("_id", uuid.toString()), toBson(), new ReplaceOptions().upsert(true));
    }

    void create(UUID uuid) {
        this.uuid = uuid;
        if (hasProfile(uuid)) {
            load(true);
            return;
        }
        this.name = "null";
        this.ip = "null";
        this.ips = new ArrayList<>();
        this.firstLogin = System.currentTimeMillis();
        this.lastSeen = System.currentTimeMillis();
        this.rank = Holiday.getInstance().getRankHandler().getDefaultRank();
        this.online = true;
        this.liked = false;
        this.inStaffMode = false;
        this.coins = 100;
        this.rankTime = -1;
        this.lowerCaseName = name.toLowerCase();
        this.messagesEnabled = true;
        Holiday.getInstance().getProfileHandler().updateProfile(this);
        save();

        Tasks.runLater(() -> reloadProfile(uuid), 15L);
    }

    void reloadProfile(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("_id", uuid)).first();

        this.uuid = uuid;

        if (document != null) {
            String ipp;
            if (document.getString("ip").equalsIgnoreCase("null")) {
                ipp = StringUtils.hash(player.getAddress().getAddress().getHostAddress());
            } else {
                ipp = document.getString("ip");
            }
            List<String> ipsL = new ArrayList<>();
            if (document.getList(ips, String.class).isEmpty()) {
                ipsL.add(StringUtils.hash(player.getAddress().getAddress().getHostAddress()));
            } else {
                ipsL.addAll(document.getList("ips", String.class));
            }
            this.name = player.getName();
            this.ip = ipp;
            this.ips = ipsL;
            this.firstLogin = document.getLong("firstLogin");
            this.lastSeen = document.getLong("lastSeen");
            this.rank = Holiday.getInstance().getRankHandler().getFromName(document.getString("rank"));
            this.liked = document.getBoolean("liked");
            this.inStaffMode = document.getBoolean("staff");
            this.online = document.getBoolean("online");
            this.rankTime = document.getLong("rankTime");
            this.coins = document.getInteger("coins");
            this.lowerCaseName = name.toLowerCase();
            this.messagesEnabled = document.getBoolean("isMessages");

            if (isDisguised()) this.disguiseData = Holiday.getInstance().getDisguiseHandler().getDisguiseData(uuid);
            Holiday.getInstance().getProfileHandler().updateProfile(this);
            save();
        }

    }

    public boolean isFrozen() {
        return getPlayer() != null && getPlayer().hasMetadata("frozen");
    }

    public Document toBson() {
        Profile profile = this;

        return new Document("_id", profile.getUuid().toString())
                .append("name", profile.getName())
                .append("ip", profile.getIp())
                .append("ips", profile.getIps())
                .append("firstLogin", profile.getFirstLogin())
                .append("lastSeen", profile.getLastSeen())
                .append("online", profile.isOnline())
                .append("rank", profile.getRank().getName())
                .append("rankTime", profile.getRankTime())
                .append("liked", profile.isLiked())
                .append("staff", profile.isInStaffMode())
                .append("coins", profile.getCoins())
                .append("server", profile.getCurrentServer())
                .append("isMessages", profile.isMessagesEnabled())
                .append("lname", profile.getName().toLowerCase());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public String getNameWithColor() {
        if (rank == null || name == null || (rank.getColor() + name).contains("null")) {
            return "&4Console";
        }
        return rank.getColor() + name;
    }


    public boolean hasEnoughCoins(int number) {
        return this.getCoins() >= number;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void removeCoins(int coins) {
        this.coins -= coins;
    }

    public List<PunishData> getPunishments() {
        return new ArrayList<>(Holiday.getInstance().getPunishmentHandler().getAllPunishmentsProfile(this));
    }

    public void checkRankTime() {
        if (this.rankTime == -1L) return;
        if (this.rankTime >= System.currentTimeMillis()) return;
        this.rankTime = -1L;
        this.rank = Holiday.getInstance().getRankHandler().getDefaultRank();
        save();
    }

    public static List<Grant> getAllGrants(UUID user) {
        List<Grant> toReturn = new ArrayList<>();
        MongoUtils.submitToThread(() -> MongoUtils.getGrantCollection()
                .find(Filters.eq("user", user.toString()))
                .forEach((Block<Document>) doc -> toReturn.add(new Grant(doc))));
        return toReturn;
    }


    public List<Profile> getAlts() {
        List<Profile> toReturn = new ArrayList<>();
        MongoUtils.getProfileCollection()
                .find(Filters.eq("ip", getIp()))
                .forEach((Block<Document>) doc -> {
                    Profile alt = new Profile(doc);
                    if (!toReturn.contains(alt)) toReturn.add(alt);
                });
        return toReturn;
    }

    public List<String> formatAlts() {
        List<String> toReturn = new ArrayList<>();
        List<Profile> profiles = new ArrayList<>(getAlts());
        profiles.forEach(profile -> {
            if (PunishmentUtils.checkBlacklisted(profile)) toReturn.add("&4" + profile.getName());
            else if (PunishmentUtils.checkIPBanned(profile) || PunishmentUtils.checkBanned(profile))
                toReturn.add("&c" + profile.getName());
            else if (PunishmentUtils.checkMuted(profile)) toReturn.add("&e" + profile.getName());
            else if (profile.isOnline()) toReturn.add("&a" + profile.getName());
            else toReturn.add("&7" + profile.getName());
        });
        return toReturn;
    }

    public List<Grant> getAllGrants() {
        List<Grant> toReturn = new ArrayList<>();
        MongoUtils.submitToThread(() -> MongoUtils.getGrantCollection()
                .find(Filters.eq("user", uuid.toString()))
                .forEach((Block<Document>) doc -> toReturn.add(new Grant(doc))));
        return toReturn;
    }


    public String getDisplayName() {
        if (this.disguiseData != null) {
            return this.disguiseData.displayName();
        } else {
            return getName();
        }
    }

    public String getDisplayNameWithColor() {
        if (this.disguiseData != null) {
            return disguiseData.disguiseRank().getColor() + disguiseData.displayName();
        } else {
            if (rank == null || name == null || (rank.getColor() + name).contains("null")) {
                return "&cConsole";
            }
            return rank.getColor() + name;
        }
    }


    public Rank getDisplayRank() {
        if (this.disguiseData != null) {
            return disguiseData.disguiseRank();
        } else {
            return rank;
        }
    }

    public boolean isDisguised() {
        return Holiday.getInstance().getDisguiseHandler().isDisguised(uuid);
    }

    public boolean isDisguisedOnLogin() {
        return Holiday.getInstance().getDisguiseHandler().isDisguisedMongo(uuid);
    }





    void loadFromDocument(Document document) {
        this.uuid = UUID.fromString(document.getString("_id"));

        this.name = document.getString("name");
        this.ip = document.getString("ip");

        this.ips = document.getList("ips", String.class);

        this.online = document.getBoolean("online");
        this.liked = document.getBoolean("liked");
        this.rank = Holiday.getInstance().getRankHandler().getFromName(document.getString("rank"));
        this.inStaffMode = document.getBoolean("staff");

        this.firstLogin = document.getLong("firstLogin");
        this.lastSeen = document.getLong("lastSeen");

        this.coins = document.getInteger("coins");
        this.currentServer = document.getString("server");
        this.rankTime = document.getLong("rankTime");
        this.lowerCaseName = name.toLowerCase();
        this.messagesEnabled = document.getBoolean("isMessages");

        if (Holiday.getInstance().getDisguiseHandler().isDisguisedMongo(uuid)) this.disguiseData = Holiday.getInstance().getDisguiseHandler().getDisguiseData(uuid);
    }


}
