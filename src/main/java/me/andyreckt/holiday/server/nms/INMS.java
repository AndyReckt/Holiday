package me.andyreckt.holiday.server.nms;

import com.mojang.authlib.GameProfile;
import org.bukkit.entity.Player;

public interface INMS {

    void removeExecute(final Player p0);

    void addExecute(final Player p0);

    void respawnPlayer(final Player p0);

    void updatePlayer(final Player p0);

    void updateCache(GameProfile p);

    GameProfile getGameProfile(final Player p0);


}
