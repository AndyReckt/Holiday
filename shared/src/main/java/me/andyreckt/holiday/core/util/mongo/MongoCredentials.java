package me.andyreckt.holiday.core.util.mongo;

public class MongoCredentials {
    private final String host;
    private final int port;
    private final boolean auth;
    private final String username;
    private final String password;

    public MongoCredentials(String host, int port) {
        this.host = host;
        this.port = port;
        this.auth = false;
        this.username = null;
        this.password = null;
    }

    public MongoCredentials(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.auth = true;
        this.username = username;
        this.password = password;
    }

    public String getURI() {
        if (!auth) return "mongodb://" + host + ":" + port;
        return "mongodb://" + username + ":" + password + "@" + host + ":" + port;
    }
}
