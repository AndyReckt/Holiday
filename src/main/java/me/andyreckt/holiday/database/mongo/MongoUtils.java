package me.andyreckt.holiday.database.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoDB;
import me.andyreckt.holiday.utils.Tasks;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.concurrent.ForkJoinPool;


public class MongoUtils {

    private static MongoDatabase getDatabase() {
        return Holiday.getInstance().getMongoDatabase();
    }

    private static MongoCollection getCollection(String collection){
        return getDatabase().getCollection(collection);
    }

    public static MongoCollection getRankCollection(){
        return getCollection("Ranks");
    }

    public static MongoCollection getProfileCollection(){
        return getCollection("Profiles");
    }

    public static MongoCollection getPunishmentsCollection() {
        return getCollection("Punishments");
    }

    public static MongoCollection getGrantCollection(){
        return getCollection("Grants");
    }

    public static MongoCollection getDisguiseCollection() {
        return getCollection("Disguises");
    }

    public static MongoCollection getServersCollection() {
        return getCollection("Servers");
    }


    public static void submitToThread(Runnable runnable) {
        Holiday.getInstance().getDbExecutor().execute(runnable);
    }

}
