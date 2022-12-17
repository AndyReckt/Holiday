package me.andyreckt.holiday.core.util.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RedisCredentials {

    private final String hostname;
    private final int port;

    private final boolean auth;
    private final String password;


    public RedisCredentials(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.auth = false;
        this.password = "foobar";
    }

}