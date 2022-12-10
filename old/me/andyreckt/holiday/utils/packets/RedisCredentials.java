package me.andyreckt.holiday.utils.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Grape (grape#9876)
 * 10/27/2021 / 9:46 PM
 * Aether / me.grape.aether.shared.credentials
 */
@AllArgsConstructor
@Getter
public class RedisCredentials {

    private final String hostname;
    private final int port;

    private final boolean auth;
    private final String password;

}