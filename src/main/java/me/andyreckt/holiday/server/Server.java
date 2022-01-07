package me.andyreckt.holiday.server;


import cc.teamfight.astria.Loader;
import cc.teamfight.astria.backend.mongo.MongoUtils;
import cc.teamfight.astria.backend.redis.Redis;
import cc.teamfight.astria.backend.redis.packet.ServerPacket;
import cc.teamfight.astria.other.enums.ServerPacketType;
import cc.teamfight.astria.player.rank.Rank;
import cc.teamfight.astria.utils.StaffUtils;
import cc.teamfight.astria.utils.StringUtils;
import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.andyreckt.holiday.player.rank.Rank;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Data
public class Server {
    String name;
    int players, maxplayers;
    boolean whitelisted;
    Rank whitelistRank;
    List<UUID> whitelistedPlayers;
    
    public static HashMap<String, ServerData> servers = new HashMap<>();

    public static ServerData fromDocument(Document document) {
        return new ServerData(
                document.getString("_id"),
                0,
                document.getInteger("maxPlayers"),
                document.getBoolean("whitelisted"),
                Rank.getByName(document.getString("whitelistRank")),
                transform(document.getList("whitelistedPlayers", String.class))
        );
    }

    public static Document toBson(ServerData data) {
        return new Document("_id", data.getName())
                .append("playerCount", data.getPlayers())
                .append("maxPlayers", data.getMaxplayers())
                .append("whitelisted", data.isWhitelisted())
                .append("whitelistRank", data.getWhitelistRank().getName())
                .append("whitelistedPlayers", transforms(data.getWhitelistedPlayers()));
    }


    public static void init() {
        ServerData data;
        if (MongoUtils.getServersCollection().find(Filters.eq("_id", Loader.getConfig().getString("serverName"))).first() == null) {
            data = new ServerData(Loader.getConfig().getString("serverName"), Bukkit.getOnlinePlayers().size(), 250, true, Rank.TRIAL, new ArrayList<>());
            StringUtils.setSlots(250);
        } else {
           data = fromDocument((Document) MongoUtils.getServersCollection().find(Filters.eq("_id", Loader.getConfig().getString("serverName"))).first());
        }
        MongoUtils.getServersCollection().replaceOne(Filters.eq("_id", data.getName()), toBson(data), new ReplaceOptions().upsert(true));
        thisServer = data;

        List<Document> docs = new ArrayList<>();
        MongoUtils.getServersCollection()
                .find()
                .forEach((Block<Document>) docs::add);
        docs.forEach(d -> servers.put(Server.fromDocument(d).getName(), Server.fromDocument(d)));
        Redis.getPidgin().sendPacket(new ServerPacket(thisServer, ServerPacketType.ADD));
    }

    public static void save() {
        update();
        MongoUtils.getServersCollection().replaceOne(Filters.eq("_id", thisServer.getName()), toBson(thisServer), new ReplaceOptions().upsert(true));
        Redis.getPidgin().sendPacket(new ServerPacket(thisServer, ServerPacketType.KEEPALIVE));
    }

    static void update() {
        thisServer.setPlayers(Bukkit.getOnlinePlayers().size());
        thisServer.setMaxplayers(Bukkit.getMaxPlayers());
    }

    public static void stop() {
        thisServer.setPlayers(0);
        //Redis.getPidgin().sendPacket(new BroadcastPacket("&c[A] &5"  + Loader.getServerName() + " &cjust went offline and wont be joinable anymore!", BroadcastType.ADMIN));
        StaffUtils.Admin.sendServerStop();
        Redis.getPidgin().sendPacket(new ServerPacket(thisServer, ServerPacketType.REMOVE));
    }

    static List<UUID> transform(List<String> strings) {
        List<UUID> toReturn = new ArrayList<>();
        strings.forEach(s -> toReturn.add(UUID.fromString(s)));
        return toReturn;
    }

    static List<String> transforms(List<UUID> strings) {
        List<String> toReturn = new ArrayList<>();
        strings.forEach(s -> toReturn.add(s.toString()));
        return toReturn;
    }

    @Data @AllArgsConstructor
    public static class ServerData {

    }
}
