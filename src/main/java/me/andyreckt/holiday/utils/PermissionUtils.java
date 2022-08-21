package me.andyreckt.holiday.utils;

import com.google.common.collect.Maps;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.grant.Grant;
import me.andyreckt.holiday.player.rank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PermissionUtils { //Initially from Exolon, edited

    public static Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    public static void updatePermissions(Player player) {
        if (getAttachment(player) != null) player.removeAttachment(getAttachment(player));
        PermissionAttachment attachment = player.addAttachment(Holiday.getInstance());
        Profile profile = Holiday.getInstance().getProfileHandler().getByUUID(player.getUniqueId());

        for (String permission : profile.getPermissions()) {
            attachment.setPermission((permission.startsWith("-") ? permission.substring(1) : permission), !permission.startsWith("-"));
        }

        for (Rank rank : profile.getActiveGrants().stream().map(Grant::getRank).collect(Collectors.toList())) {
            for (String permission : rank.getPermissions()) {
                String key = permission.startsWith("-") ? permission.substring(1) : permission;
                if (!attachment.getPermissions().containsKey(key)) {
                    attachment.setPermission(key, !permission.startsWith("-"));
                }
            }

            for (Rank subRank : rank.getChilds().stream().map(uid -> Holiday.getInstance().getRankHandler().getFromId(uid)).collect(Collectors.toList())) {
                for (String permission : subRank.getPermissions()) {
                    String key = permission.startsWith("-") ? permission.substring(1) : permission;
                    if (!attachment.getPermissions().containsKey(key)) {
                        attachment.setPermission(key, !permission.startsWith("-"));
                    }
                }
            }
        }

        attachments.put(player.getUniqueId(), attachment);
        player.recalculatePermissions();
    }

    public static PermissionAttachment getAttachment(Player player) {
        return attachments.getOrDefault(player.getUniqueId(), null);
    }

}
