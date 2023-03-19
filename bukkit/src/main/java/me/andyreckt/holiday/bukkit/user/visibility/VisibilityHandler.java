package me.andyreckt.holiday.bukkit.user.visibility;

import org.bukkit.entity.Player;

import java.util.List;

public interface VisibilityHandler {

    void updateAll();

    void updateToAll(Player player);

    void updateAllTo(Player player);

    void update(Player player);

    void update(Player viewer, Player target);

    boolean treatAsOnline(Player viewer, Player target);

    boolean canSee(Player viewer, Player target);

    List<Player> getAllVisibleTo(Player player);

    List<Player> getAllOnlineTo(Player player);

}
