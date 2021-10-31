package me.andyreckt.holiday.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.andyreckt.holiday.Files;
import me.andyreckt.holiday.Holiday;

public class MongoDB {

    @Getter static MongoClient mongoClient;
    @Getter static MongoDatabase database;


    public MongoDB() {
        mongoClient = new MongoClient(new MongoClientURI(Files.Config.MONGO_URI.getString()));
        database = mongoClient.getDatabase("Holiday");
    }


}
