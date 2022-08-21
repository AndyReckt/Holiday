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

    private static MongoCollection<Document> getCollection(String collection){
        return getDatabase().getCollection(collection);
    }

    public static MongoCollection<Document> getRankCollection(){
        return getCollection("Ranks");
    }

    public static MongoCollection<Document> getProfileCollection(){
        return getCollection("Profiles");
    }

    public static MongoCollection<Document> getPunishmentsCollection() {
        return getCollection("Punishments");
    }

    public static MongoCollection<Document> getGrantCollection(){
        return getCollection("Grants");
    }

    public static MongoCollection<Document> getDisguiseCollection() {
        return getCollection("Disguises");
    }

    public static MongoCollection<Document> getServersCollection() {
        return getCollection("Servers");
    }


    public static void submitToThread(Runnable runnable) {
        Holiday.getInstance().getDbExecutor().execute(runnable);
    }

}
