package me.andyreckt.holiday.server;

import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.database.redis.packet.ServerPacket;
import me.andyreckt.holiday.other.enums.ServerPacketType;
import me.andyreckt.holiday.player.rank.RankHandler;
import me.andyreckt.holiday.utils.StringUtils;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.*;

@Getter
public class ServerHandler {

    Map<String, Server> servers;
    Server thisServer;

    final Holiday plugin;

    public ServerHandler(Holiday plugin) {
        this.plugin = plugin;
        init();
    }

    void init() {
        Server data;
        servers = new HashMap<>();

        if (MongoUtils.getServersCollection().find(Filters.eq("_id", plugin.getSettings().getString("SERVER.NAME"))).first() == null) {
            data = new Server(plugin.getSettings().getString("SERVER.NAME"), Bukkit.getOnlinePlayers().size(), 250, false, plugin.getRankHandler().getDefaultRank(), new ArrayList<>());
            StringUtils.setSlots(250);
        } else {
            data = fromDocument((Document) MongoUtils.getServersCollection().find(Filters.eq("_id", plugin.getSettings().getString("SERVER.NAME"))).first());
        }
        MongoUtils.getServersCollection().replaceOne(Filters.eq("_id", data.getName()), toBson(data), new ReplaceOptions().upsert(true));
        thisServer = data;

        List<Document> docs = new ArrayList<>();
        MongoUtils.getServersCollection()
                .find()
                .forEach((Block<Document>) docs::add);
        docs.forEach(d -> servers.put(fromDocument(d).getName(), fromDocument(d)));
        plugin.getRedis().sendPacket(new ServerPacket(thisServer, ServerPacketType.ADD));
    }


    public Server fromDocument(Document document) {
        RankHandler rh = plugin.getRankHandler();
        return new Server(
                document.getString("_id"),
                0,
                document.getInteger("maxPlayers"),
                document.getBoolean("whitelisted"),
                rh.getFromName(document.getString("whitelistRank")),
                transform(document.getList("whitelistedPlayers", String.class))
        );
    }

    List<UUID> transform(List<String> strings) {
        List<UUID> toReturn = new ArrayList<>();
        strings.forEach(s -> toReturn.add(UUID.fromString(s)));
        return toReturn;
    }
    List<String> transforms(List<UUID> strings) {
        List<String> toReturn = new ArrayList<>();
        strings.forEach(s -> toReturn.add(s.toString()));
        return toReturn;
    }

    Document toBson(Server data) {
        return new Document("_id", data.getName())
                .append("playerCount", data.getPlayers())
                .append("maxPlayers", data.getMaxplayers())
                .append("whitelisted", data.isWhitelisted())
                .append("whitelistRank", data.getWhitelistRank().getName())
                .append("whitelistedPlayers", transforms(data.getWhitelistedPlayers()));
    }


    public void stop() { //TODO MESSAGE
        thisServer.setPlayers(0);
        //Redis.getPidgin().sendPacket(new BroadcastPacket("&c[A] &5"  + Loader.getServerName() + " &cjust went offline and wont be joinable anymore!", BroadcastType.ADMIN));
        //StaffUtils.Admin.sendServerStop();
        plugin.getRedis().sendPacket(new ServerPacket(thisServer, ServerPacketType.REMOVE));
    }

    public void save() {
        update();
        MongoUtils.getServersCollection().replaceOne(Filters.eq("_id", thisServer.getName()), toBson(thisServer), new ReplaceOptions().upsert(true));
        Holiday.getInstance().getRedis().sendPacket(new ServerPacket(thisServer, ServerPacketType.KEEPALIVE));
    }

    void update() {
        thisServer.setPlayers(Bukkit.getOnlinePlayers().size());
        thisServer.setMaxplayers(Bukkit.getMaxPlayers());
    }

}
