package me.andyreckt.holiday.staff.commands;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.other.Cooldown;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import me.andyreckt.holiday.staff.Staff;
import me.andyreckt.holiday.staff.server.StaffListMenu;
import me.andyreckt.holiday.staff.util.files.SLocale;
import me.andyreckt.holiday.staff.util.files.SPerms;
import me.andyreckt.holiday.staff.util.sunset.annotations.Command;
import me.andyreckt.holiday.staff.util.sunset.annotations.Param;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Commands {

    private Map<UUID, Cooldown> cooldownMap = new ConcurrentHashMap<>();

    @Command(names = {"staffmode", "staff", "modmode", "mod"}, permission = SPerms.STAFF, description = "Toggle staff mode.")
    public void staff(Player player) {

        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
        boolean modmodded = profile.getStaffSettings().isStaffMode();
        SLocale locale;

        if (modmodded) locale = SLocale.ALERTS_MODMODE_OFF;
        else locale = SLocale.ALERTS_MODMODE_ON;

        String toSend = locale.getString()
                .replace("%player%", UserConstants.getNameWithColor(profile))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName());

        PacketHandler.send(new BroadcastPacket(toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
        Staff.getInstance().getStaffManager().toggleStaffMode(player);
    }

    @Command(names = {"vanish", "v"}, permission = SPerms.STAFF, description = "Toggle vanish.")
    public void vanish(Player player) {
        if (cooldown(player.getUniqueId())) return;
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
        boolean vanished = profile.getStaffSettings().isVanished();
        SLocale locale;

        if (vanished) locale = SLocale.ALERTS_VANISH_OFF;
        else locale = SLocale.ALERTS_VANISH_ON;

        String toSend = locale.getString()
                            .replace("%player%", UserConstants.getNameWithColor(profile))
                            .replace("%server%", Holiday.getInstance().getThisServer().getServerName());

        PacketHandler.send(new BroadcastPacket(toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
        Staff.getInstance().getStaffManager().vanish(player, profile.getStaffSettings().isStaffMode());
    }

    @Command(names = "build", permission = SPerms.BUILD, description = "Toggle build mode.")
    public void build(Player player) {
        if (cooldown(player.getUniqueId())) return;
        if (player.hasMetadata("staff.build")) {
            player.removeMetadata("staff.build", Staff.getInstance());
            player.sendMessage(SLocale.BUILD_DISABLED.getString());
        } else {
            player.setMetadata("staff.build", new FixedMetadataValue(Staff.getInstance(), true));
            player.sendMessage(SLocale.BUILD_ENABLED.getString());
        }
    }

    @Command(names = {"freeze", "ss"}, permission = SPerms.FREEZE, description = "Freeze a player.")
    public void freeze(Player player, @Param(name = "target") Player target) {
        if (cooldown(player.getUniqueId())) return;
        if (target.hasMetadata("frozen")) {
            target.removeMetadata("frozen", Staff.getInstance());
            player.sendMessage(SLocale.FREEZE_UNFROZEN.getString().replace("%player%", target.getName()));
            target.sendMessage(SLocale.FREEZE_UNFROZEN_TARGET.getString());
        } else {
            target.setMetadata("frozen", new FixedMetadataValue(Staff.getInstance(), true));
            player.sendMessage(SLocale.FREEZE_FROZEN.getString().replace("%player%", target.getName()));
            SLocale.FREEZE_RECURRENT_MESSAGE.getStringListNetwork().forEach(target::sendMessage);
        }

        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
        Profile targetProfile = Holiday.getInstance().getApi().getProfile(target.getUniqueId());
        SLocale locale = target.hasMetadata("frozen") ? SLocale.ALERTS_FREEZE_ON : SLocale.ALERTS_FREEZE_OFF;

        String toSend = locale.getString()
                .replace("%player%", UserConstants.getNameWithColor(profile))
                .replace("%target%", UserConstants.getNameWithColor(targetProfile))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName());

        PacketHandler.send(new BroadcastPacket(toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));

    }

    @Command(names = "stafflist", permission = SPerms.LIST, description = "View the staff list.")
    public void staffList(Player player) {
        new StaffListMenu().openMenu(player);
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

}
