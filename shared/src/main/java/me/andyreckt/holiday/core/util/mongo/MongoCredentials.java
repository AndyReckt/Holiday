package me.andyreckt.holiday.core.util.mongo;

import lombok.Getter;

public class MongoCredentials {
    private final String host;
    private final int port;
    private final boolean auth;
    private final String uri;
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
        this.uri = null;
    }

    public MongoCredentials(String host, int port, String username, String password, String database) {
        this.host = host;
        this.port = port;
        this.auth = true;
        this.username = username;
        this.password = password;
        this.database = database;
        this.uri = null;
    }

    public MongoCredentials(String uri, String database) {
        this.host = null;
        this.port = 0;
        this.auth = false;
        this.username = null;
        this.password = null;
        this.database = database;
        this.uri = uri;
    }

    public String getURI() {
        if (uri != null) return uri;
        if (!auth) return "mongodb://" + host + ":" + port;
        return "mongodb://" + username + ":" + password + "@" + host + ":" + port;
    }
}
