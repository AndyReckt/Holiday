package me.andyreckt.holiday.bukkit.util.player;

import lombok.Getter;
import me.andyreckt.holiday.api.user.IGrant;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
//OLD
public class PermissionUtils {

    @Getter
    public static final Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    public static void updatePermissions(UUID uuid) {
        Profile profile = Holiday.getInstance().getApi().getProfile(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        if (attachments.containsKey(uuid)) {
            player.getEffectivePermissions().forEach(permission -> {
                if (permission.getAttachment() == attachments.get(uuid)) {
                    player.removeAttachment(attachments.get(uuid));
                }
            });

            attachments.remove(uuid);
        }

        PermissionAttachment attachment = player.addAttachment(Holiday.getInstance());

        for (String permission : profile.getPermissions()) {
            String perm = permission.startsWith("-") ? permission.substring(1) : permission;
            attachment.setPermission(perm, !permission.startsWith("-"));
        }

        for (IGrant permission : profile.getActiveGrants()) {
            for (String perm : permission.getRank().getPermissions()) {
                String key = perm.startsWith("-") ? perm.substring(1) : perm;
                if (!player.hasPermission(key)) {
                    attachment.setPermission(key, !perm.startsWith("-"));
                }
            }

            for (IRank child : permission.getRank().getChilds().stream().map(Holiday.getInstance().getApi()::getRank).collect(Collectors.toList())) {
                for (String perm : child.getPermissions()) {
                    String key = perm.startsWith("-") ? perm.substring(1) : perm;
                    if (!player.hasPermission(key)) {
                        attachment.setPermission(key, !perm.startsWith("-"));
                    }
                }
            }
        }

        attachments.put(uuid, attachment);
        player.recalculatePermissions();
    }

}

