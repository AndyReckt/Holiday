package me.andyreckt.holiday.bukkit.util.uuid.impl;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.uuid.UUIDCache;

import java.util.UUID;

public final class BukkitUUIDCache implements UUIDCache {

    @Override
    public UUID uuid(String name) {
        return Holiday.getInstance().getServer().getOfflinePlayer(name).getUniqueId();
    }

    @Override
    public String name(UUID uuid) {
        return Holiday.getInstance().getServer().getOfflinePlayer(uuid).getName();
    }

    @Override
    public void ensure(UUID uuid) {
    }

    @Override
    public void update(UUID uuid, String name) {
    }
}

