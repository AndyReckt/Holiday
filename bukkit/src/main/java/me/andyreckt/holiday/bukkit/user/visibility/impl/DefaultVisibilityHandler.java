package me.andyreckt.holiday.bukkit.user.visibility.impl;

import me.andyreckt.holiday.api.HolidayAPI;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.visibility.VisibilityHandler;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultVisibilityHandler implements VisibilityHandler {

    private final Holiday plugin;

    public DefaultVisibilityHandler(Holiday plugin) {
        this.plugin = plugin;
    }

    @Override
    public void updateAll() {
        plugin.getServer().getOnlinePlayers().forEach(this::updateToAll);
    }

    @Override
    public void updateToAll(Player player) {
        plugin.getServer().getOnlinePlayers().forEach(viewer -> update(viewer, player));
    }

    @Override
    public void updateAllTo(Player player) {
        plugin.getServer().getOnlinePlayers().forEach(target -> update(player, target));
    }

    @Override
    public void update(Player player) {
        updateToAll(player);
        updateAllTo(player);
    }

    @Override
    public void update(Player viewer, Player target) {
        if (canSee(viewer, target)) {
            viewer.showPlayer(target);
        } else {
            viewer.hidePlayer(target);
        }
    }

    @Override
    public boolean treatAsOnline(Player viewer, Player target) {
        Profile targetProfile = HolidayAPI.getInstance().getProfile(target.getUniqueId());

        if (targetProfile.getStaffSettings().isVanished()) return viewer.hasPermission("staff.see");

        return target.isOnline();
    }

    @Override
    public boolean canSee(Player viewer, Player target) {
        return treatAsOnline(viewer, target);
    }

    @Override
    public List<Player> getAllVisibleTo(Player player) {
        return plugin.getServer().getOnlinePlayers().stream().filter(target -> canSee(player, target)).collect(Collectors.toList());
    }

    @Override
    public List<Player> getAllOnlineTo(Player player) {
        return plugin.getServer().getOnlinePlayers().stream().filter(target -> treatAsOnline(player, target)).collect(Collectors.toList());
    }
}
