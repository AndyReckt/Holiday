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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter @Setter
public class Profile {

    @Getter static Profile instance;

    UUID uuid;
    String name, ip;
    List<String> ips;

    boolean frozen, vanished, online;

    Date firstLogin, lastSeen;

    Rank highestRank, currentVisibleRank;
    List<Grant> grants;


    public Profile() {
        instance = this;
    }


    public Profile(Player player) {
        if(hasProfile(player.getUniqueId())) getFromPlayer(player);
        else {
            create(player);
        }
    }

    public Profile(UUID uuid) {
        if(hasProfile(uuid)) getFromUUID(uuid);
        else {
            create(uuid);
        }
    }

    public Profile getFromPlayer(Player player) {
        this.uuid = player.getUniqueId();
        load();
        return this;
    }

    public Profile getFromUUID(UUID uuid) {
        this.uuid = uuid;
        load();
        return this;
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

            this.firstLogin = document.getDate("firstLogin");
            this.lastSeen = document.getDate("lastSeen");

        });
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
        this.name = player.getName();
        this.ip = player.getAddress().getAddress().getHostAddress();
        this.ips = ipsL;
        this.firstLogin = new Date();
        this.lastSeen = new Date();
        save();

    }

    public void create(UUID uuid) {
        this.uuid = uuid;
        if(hasProfile(uuid)) {
            load();
            return;
        }

        Player player = (Player) Bukkit.getOfflinePlayer(uuid);

        List<String> ipsL = new ArrayList<>();
        ipsL.add(player.getAddress().getAddress().getHostAddress());
        this.name = player.getName();
        this.ip = player.getAddress().getAddress().getHostAddress();
        this.ips = ipsL;
        this.firstLogin = new Date();
        this.lastSeen = new Date();
        save();
    }

    public static List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<>();

        Bukkit.getOnlinePlayers().forEach(player -> profiles.add(Profile.getInstance().getFromPlayer(player)));

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
