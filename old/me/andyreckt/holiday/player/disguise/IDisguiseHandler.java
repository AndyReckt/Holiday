package me.andyreckt.holiday.player.disguise;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.andyreckt.holiday.player.rank.Rank;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface IDisguiseHandler {

    void disguise(Player player, Rank rank, String skin, String name, boolean sendRequest);
    void undisguise(Player player, boolean sendRequest);
    Object loadGameProfile(UUID uniqueId, String skinName);
    boolean isDisguised(UUID uuid);
    boolean isDisguisedMongo(UUID uuid);
    boolean isDisguised(Player player);
    DisguiseData getDisguiseData(UUID player);
    Object getGameProfile(Player player);
    void updateCache(Object gameProfile);


    @Getter
    @Setter
    @Accessors(fluent = true)
    class DisguiseData {

        String displayName;
        String skinName;
        Rank disguiseRank;
        UUID uuid;
        String lDisplayName;

    }

}
