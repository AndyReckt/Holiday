package me.andyreckt.holiday.bukkit.user.permission;

import lombok.Getter;
import me.andyreckt.holiday.bukkit.util.nms.ReflectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionManager {
    @Getter
    private final Map<UUID, PermissibleBase> permissibles;
    private Field HUMAN_ENTITY_PERMISSIBLE_FIELD;


    public PermissionManager() {
        this.permissibles = new ConcurrentHashMap<>();
        try {
            HUMAN_ENTITY_PERMISSIBLE_FIELD = Class.forName("org.bukkit.craftbukkit." + ReflectionUtils.VERSION + ".entity.CraftHumanEntity").getDeclaredField("perm");
            HUMAN_ENTITY_PERMISSIBLE_FIELD.setAccessible(true);

            Field ATTACHMENTS_FIELD = PermissibleBase.class.getDeclaredField("attachments");
            ATTACHMENTS_FIELD.setAccessible(true);

            Field PERMISSIBLE_BASE_ATTACHMENTS_FIELD = PermissibleBase.class.getDeclaredField("attachments");
            PERMISSIBLE_BASE_ATTACHMENTS_FIELD.setAccessible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initPlayer(Player player) throws IllegalAccessException {
        UserPermissible permissible = new UserPermissible(player);
        HUMAN_ENTITY_PERMISSIBLE_FIELD.set(player, permissible);
        this.permissibles.put(player.getUniqueId(), permissible);
    }

}
