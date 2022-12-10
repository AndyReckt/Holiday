package me.andyreckt.holiday.core.util.mongo;

import lombok.Getter;

public class MongoCredentials {
    private final String host;
    private final int port;
    private final boolean auth;
    private final String username;
    private final String password;
    @Getter
    private final String database;

    public MongoCredentials(String host, int port, String database) {
        this.host = host;
        this.port = port;
        this.auth = false;
        this.username = null;
        this.password = null;
        this.database = database;
    }

    public MongoCredentials(String host, int port, String username, String password, String database) {
        this.host = host;
        this.port = port;
        this.auth = true;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public String getURI() {
        if (!auth) return "mongodb://" + host + ":" + port;
        return "mongodb://" + username + ":" + password + "@" + host + ":" + port;
    }
}
