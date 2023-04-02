package me.andyreckt.holiday.staff.user;

import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.other.Tasks;
import me.andyreckt.holiday.staff.Staff;
import me.andyreckt.holiday.staff.util.files.SLocale;
import me.andyreckt.holiday.staff.util.files.SPerms;
import me.andyreckt.holiday.staff.util.item.Items;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaffManager {

    private final Staff plugin;
    private final API api;
    private final Map<UUID, PlayerData> playerDataMap;


    public StaffManager(Staff plugin) {
        this.plugin = plugin;
        this.api = Holiday.getInstance().getApi();
        this.playerDataMap = new HashMap<>();
    }


    public void toggleStaffMode(Player player) {
        this.toggleStaffMode(player, !playerDataMap.containsKey(player.getUniqueId()), true);
    }

    public void toggleStaffMode(Player player, boolean bool, boolean save) {
        if (!bool) {
            this.clearPlayer(player);
            PlayerData data = playerDataMap.remove(player.getUniqueId());
            if (SLocale.AUTOMATIC_VANISH.getBoolean()) {
                this.vanish(player, false, false);
            }

            try {
                if (SLocale.TELEPORT_BACK.getBoolean()) player.teleport(data.getLocation());
            } catch (Exception ignored) {}
            player.getInventory().setContents(data.getInvContents());
            player.getInventory().setArmorContents(data.getArmorContents());
            player.setGameMode(data.getGameMode());
            player.setHealth(data.getHealth());
            player.setExp(data.getXp());
            player.setLevel(data.getLevel());
            player.setFlying(data.isFlying());
            try {
                player.setAllowFlight(data.isAllowFlight());
            } catch (Exception ignored) {}
            for (PotionEffect effect : data.getEffects()) {
                player.addPotionEffect(effect);
            }
            player.setFlySpeed(data.getFlySpeed());
            player.setWalkSpeed(data.getWalkSpeed());
            player.sendMessage(SLocale.STAFF_MOD_DISABLED.getString());
        } else {
            PlayerData data = new PlayerData(player);
            playerDataMap.put(player.getUniqueId(), data);
            this.clearPlayer(player);
            player.setGameMode(GameMode.CREATIVE);
            this.updateItems(player, true);

            if (SLocale.AUTOMATIC_VANISH.getBoolean()) {
                this.vanish(player, true, true);
            }
            player.sendMessage(SLocale.STAFF_MOD_ENABLED.getString());
        }
        if (save) {
            Tasks.runAsyncLater(() -> {
                Profile profile = api.getProfile(player.getUniqueId());
                profile.getStaffSettings().setStaffMode(bool);
                api.saveProfile(profile);
            }, 3L);
        }
    }

    public void vanish(Player player, boolean items) {
        boolean old = api.getProfile(player.getUniqueId()).getStaffSettings().isVanished();
        this.vanish(player, !old, items);
    }

    public void vanish(Player player, boolean bool, boolean items) {
        Profile profile = api.getProfile(player.getUniqueId());

        SLocale locale = !bool ? SLocale.VANISH_OFF : SLocale.VANISH_ON;

        player.sendMessage(locale.getString());
        profile.getStaffSettings().setVanished(bool);
        api.saveProfile(profile);

        this.updateVisibility(player);
        if (items) this.updateItems(player, false);
    }

    private void updateVisibility(Player player) {
        if (!SLocale.UPDATE_VISIBILITY.getBoolean()) return;
        Holiday.getInstance().getVisibilityHandler().updateToAll(player);

//        boolean vanished = api.getProfile(player.getUniqueId()).getStaffSettings().isVanished();
//
//        for (Player online : plugin.getServer().getOnlinePlayers()) {
//            if (online.hasPermission(SPerms.SEE_VANISHED.get())) {
//                online.showPlayer(player);
//                continue;
//            }
//
//            if (vanished) online.hidePlayer(player);
//            else online.showPlayer(player);
//        }
    }

    public void updateItems(Player player, boolean first) {
        boolean vanished = api.getProfile(player.getUniqueId()).getStaffSettings().isVanished();

        if (first) {
            player.getInventory().setHeldItemSlot(4);
            player.getInventory().setItem(4, Items.FREEZE.getItem());
            player.getInventory().setItem(0, Items.INVSEE.getItem());
            player.getInventory().setItem(1, Items.BETTER_VIEW.getItem());
            player.getInventory().setItem(7, Items.STAFF_LIST.getItem());
        }

        player.getInventory().setItem(8, vanished ? Items.GO_VISIBLE.getItem() : Items.GO_INVISIBLE.getItem());
        player.updateInventory();
    }

    private void clearPlayer(Player player) {
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setSaturation(12.8F);
        player.setMaximumNoDamageTicks(20);
        player.setFireTicks(0);
        player.setFallDistance(0.0F);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.1F);
        player.getInventory().setHeldItemSlot(0);
        player.setAllowFlight(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.closeInventory();
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        Holiday.getInstance().getNms().clearDataWatcher(player);
        player.getOpenInventory().getTopInventory().clear();
        player.updateInventory();
    }

}
