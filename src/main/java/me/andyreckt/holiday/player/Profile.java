package me.andyreckt.holiday.player;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.database.utils.MongoUtils;
import me.andyreckt.holiday.rank.Rank;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class Profile {

    @Getter static Profile instance;

    UUID uuid;
    String name, ip;
    List<String> ips;

    boolean frozen, vanished;

    Date firstLogin, lastSeen;

    Rank rank;


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

    public void load() {
        MongoUtils.getExecutor().execute(() -> {
            Document document = (Document) MongoUtils.getRankCollection().find(Filters.eq("_id", uuid.toString())).first();

            this.name = document.getString("name");
            this.ip = document.getString("ip");

            this.ips = document.getList("ips", String.class);

            this.firstLogin = document.getDate("firstLogin");
            this.lastSeen = document.getDate("lastSeen");
        });
    }

    public void save() {
        MongoUtils.getExecutor().execute(() -> MongoUtils.getRankCollection().replaceOne(Filters.eq("_id", uuid.toString()), toBson(), new ReplaceOptions().upsert(true)));
    }

    public void create(Player player) {
        this.uuid = player.getUniqueId();
        if(hasProfile(uuid)) {
            load();
            return;
        }

        MongoUtils.getExecutor().execute(() -> {
            Document document = new Document("_id", player.getUniqueId().toString())
                    .append("name", player.getName())
                    .append("ip", player.getAddress().getAddress().getHostAddress())
                    .append("ips", new ArrayList<String>().add(player.getAddress().getAddress().getHostAddress()))
                    .append("firstLogin", new Date())
                    .append("lastSeen", new Date());

            MongoUtils.getRankCollection().replaceOne(Filters.eq("_id", uuid.toString()), document, new ReplaceOptions().upsert(true));
        });
    }

    public void create(UUID uuid) {
        this.uuid = uuid;
        if(hasProfile(uuid)) {
            load();
            return;
        }

        Player player = (Player) Bukkit.getOfflinePlayer(uuid);

        MongoUtils.getExecutor().execute(() -> {
            Document document = new Document("_id", player.getUniqueId().toString())
                    .append("name", player.getName())
                    .append("ip", player.getAddress().getAddress().getHostAddress())
                    .append("ips", new ArrayList<String>().add(player.getAddress().getAddress().getHostAddress()))
                    .append("firstLogin", new Date())
                    .append("lastSeen", new Date());

            MongoUtils.getRankCollection().replaceOne(Filters.eq("_id", uuid.toString()), document, new ReplaceOptions().upsert(true));
        });
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
