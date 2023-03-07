package me.andyreckt.holiday.core.util.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.andyreckt.holiday.core.HolidayAPI;
import org.bson.Document;

@Getter
public class MongoManager {
    private final HolidayAPI api;

    private final MongoClient client;
    private final MongoDatabase database;

    private final MongoCollection<Document> profiles;
    private final MongoCollection<Document> ranks;
    private final MongoCollection<Document> grants;
    private final MongoCollection<Document> punishments;

    public MongoManager(HolidayAPI api, MongoCredentials credentials) {
        this.api = api;

        this.client = new MongoClient(new MongoClientURI(credentials.getURI()));
        this.database = this.client.getDatabase(credentials.getDatabase());

        this.profiles = database.getCollection("profiles");
        this.ranks = database.getCollection("ranks");
        this.grants = database.getCollection("grants");
        this.punishments = database.getCollection("punishments");
    }
}
