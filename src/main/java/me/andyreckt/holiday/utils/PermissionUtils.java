package me.andyreckt.holiday.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.grant.Grant;
import me.andyreckt.holiday.player.rank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PermissionUtils { //Initially from Exolon, edited

    private static final Map<UUID, PermissionAttachment> attachments = Maps.newHashMap();

    public static void updatePermissions(Player player){
        try {
            try {
                if (getAttachment(player) != null) player.removeAttachment(getAttachment(player));
            } catch (IllegalArgumentException ignored) {}
            PermissionAttachment attachment = player.addAttachment(Holiday.getInstance());
            Profile profile = Holiday.getInstance().getProfileHandler().getByUUID(player.getUniqueId());
            for (String permission : profile.getPermissions()) {
                attachment.setPermission((permission.startsWith("-") ? permission.substring(1) : permission), !permission.startsWith("-"));
            }
            for (Grant grant : profile.getActiveGrants()) {

                Rank rank = grant.getRank();
                for (String permission : rank.getPermissions()) {
                    String key = permission.startsWith("-") ? permission.substring(1) : permission;
                    if (attachment.getPermissions().containsKey(key))  {
                        if (!(attachment.getPermissions().get(key)) && (!permission.startsWith("-"))) attachment.setPermission(permission, true);
                    } else
                    attachment.setPermission(key, !permission.startsWith("-"));
                }

                for (UUID id : rank.getChilds()) {
                    for (String permission : Holiday.getInstance().getRankHandler().getFromId(id).getPermissions()) {
                        String key = permission.startsWith("-") ? permission.substring(1) : permission;
                        if (attachment.getPermissions().containsKey(key))  {
                            if (!(attachment.getPermissions().get(key)) && (!permission.startsWith("-"))) attachment.setPermission(permission, true);
                        } else
                            attachment.setPermission(key, !permission.startsWith("-"));
                    }
                }
            }
            attachments.put(player.getUniqueId(), attachment);
            player.recalculatePermissions();
        } catch(ConcurrentModificationException ignored){}
    }

    public static PermissionAttachment getAttachment(Player player){
        return attachments.getOrDefault(player.getUniqueId(), null);
    }

}
