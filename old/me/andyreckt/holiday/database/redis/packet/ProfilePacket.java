package me.andyreckt.holiday.database.redis.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.packets.Packet;

@Getter
@AllArgsConstructor
public class ProfilePacket implements Packet {

    Profile profile;

    @AllArgsConstructor @Getter
    public static class ProfileDeletePacket implements Packet {

        Profile profile;

    }

    @AllArgsConstructor @Getter
    public static class ProfileMessagePacket implements Packet {

        Profile profile;
        String message;

    }
}