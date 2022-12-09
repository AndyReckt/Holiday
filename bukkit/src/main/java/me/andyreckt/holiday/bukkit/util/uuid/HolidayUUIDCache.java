package me.andyreckt.holiday.bukkit.util.uuid;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.uuid.impl.RedisUUIDCache;

import java.util.UUID;


public final class HolidayUUIDCache {

    @Getter private static UUIDCache impl = null;
    private static boolean initiated = false;

    private HolidayUUIDCache() {
    }

    public static void init() {
        Preconditions.checkState(!initiated);
        initiated = true;
        try {
            impl = new RedisUUIDCache();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Holiday.getInstance().getServer().getPluginManager().registerEvents(new UUIDListener(), Holiday.getInstance());
    }

    public static UUID uuid(String name) {
        return impl.uuid(name);
    }

    public static String name(UUID uuid) {
        return impl.name(uuid);
    }

    public static void ensure(UUID uuid) {
        impl.ensure(uuid);
    }

    public static void update(UUID uuid, String name) {
        impl.update(uuid, name);
    }

}

