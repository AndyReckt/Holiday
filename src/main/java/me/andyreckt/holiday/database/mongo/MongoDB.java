package me.andyreckt.holiday.database.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;

@Getter
public class MongoDB {

    MongoClient mongoClient;
    MongoDatabase database;


    public MongoDB(BasicConfigurationFile config) {
        mongoClient = new MongoClient(new MongoClientURI(config.getString("MONGO.URI")));
        database = mongoClient.getDatabase(config.getString("MONGO.DBNAME"));
    }


}
