package me.andyreckt.holiday.staff.commands;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.other.Cooldown;
import me.andyreckt.holiday.staff.Staff;
import me.andyreckt.holiday.staff.util.files.SLocale;
import me.andyreckt.holiday.staff.util.files.SPerms;
import me.andyreckt.holiday.staff.util.sunset.annotations.Command;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Commands {

    private Map<UUID, Cooldown> cooldownMap = new ConcurrentHashMap<>();

    @Command(names = {"staffmode", "staff", "modmode", "mod"}, permission = SPerms.STAFF, description = "Toggle staff mode.")
    public void staff(Player player) {
        Staff.getInstance().getStaffManager().toggleStaffMode(player);
    }

    @Command(names = {"vanish", "v"}, permission = SPerms.STAFF, description = "Toggle vanish.")
    public void vanish(Player player) {
        if (cooldown(player.getUniqueId())) return;

        Staff.getInstance().getStaffManager().vanish(player,
                Holiday.getInstance().getApi().getProfile(player.getUniqueId()).getStaffSettings().isStaffMode());
    }

    @Command(names = "build", permission = SPerms.BUILD, description = "Toggle build mode.")
    public void build(Player player) {
        if (cooldown(player.getUniqueId())) return;
        if (player.hasMetadata("staff.build")) {
            player.removeMetadata("staff.build", Holiday.getInstance());
            player.sendMessage(SLocale.BUILD_DISABLED.getString());
        } else {
            player.setMetadata("staff.build", new FixedMetadataValue(Holiday.getInstance(), true));
            player.sendMessage(SLocale.BUILD_ENABLED.getString());
        }
    }

    private boolean cooldown(UUID uuid) {
        if (cooldownMap.containsKey(uuid)) {
            Cooldown cooldown = cooldownMap.get(uuid);
            if (cooldown.hasExpired()) {
                cooldownMap.remove(uuid);
                return false;
            } else {
                return true;
            }
        } else {
            cooldownMap.put(uuid, new Cooldown(2000L));
            return false;
        }
    }

    //TODO: freeze

}
