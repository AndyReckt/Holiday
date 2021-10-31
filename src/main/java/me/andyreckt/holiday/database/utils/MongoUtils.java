package me.andyreckt.holiday.database.utils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.MongoDB;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.concurrent.Executor;


public class MongoUtils {

    public static MongoDatabase getDatabase() {
        return MongoDB.getDatabase();
    }

    public static MongoCollection getCollection(String collection){
        return MongoDB.getDatabase().getCollection(collection);
    }

    public static MongoCollection getRankCollection(){
        return MongoDB.getDatabase().getCollection("Ranks");
    }



    public static MongoCollection getProfileCollection(){
        return MongoDB.getDatabase().getCollection("Profiles");
    }

    public static MongoCollection getPunishmentsCollection() {
        return MongoDB.getDatabase().getCollection("Punishments");
    }

    public static MongoCollection getGrantCollection(){
        return MongoDB.getDatabase().getCollection("Grants");
    }

    public static Object getFromCollection(Player player, String string, String collection) throws Exception {
        Document query = new Document("uuid", player.getUniqueId());
        Document toReturn = (Document) getCollection(collection).find(query).first();
        if(toReturn != null) return toReturn.get(string);
        else throw new Exception("This player isn't in the database");
    }

    public static Executor getExecutor() {
        return Holiday.getInstance().getDbExecutor();
    }

}
