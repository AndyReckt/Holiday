package me.andyreckt.holiday.bukkit.server.nms;

import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.core.user.disguise.Disguise;
import org.bukkit.entity.Player;

public interface INMS {

    void removeExecute(final Player p0);

    void addExecute(final Player p0);

    void respawnPlayer(final Player p0);

    void updatePlayer(final Player p0);

    void clearDataWatcher(final Player p0);

    double[] recentTps();

    void disguise(Disguise disguise);

    void unDisguise(Disguise disguise);

    ItemBuilder insertSkinPropertyFromHash(String hash, ItemBuilder builder);

    void sendDemoScreen(Player target);
}
