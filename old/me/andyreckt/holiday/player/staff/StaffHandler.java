package me.andyreckt.holiday.player.staff;

import com.lunarclient.bukkitapi.LunarClientAPI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.player.staff.event.StaffModeEnterEvent;
import me.andyreckt.holiday.player.staff.event.StaffModeLeaveEvent;
import me.andyreckt.holiday.player.staff.event.StaffUpdateVisibilityEvent;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.PlayerUtil;
import me.andyreckt.holiday.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class StaffHandler {
    @Getter
    private static Map<UUID, StaffData> staffs = new HashMap<>();

    private final Holiday holiday;

    public StaffHandler(Holiday holiday) {
        this.holiday = holiday;
    }

    public StaffData getStaffPlayer(Player player) {
        if (player == null) return null;
        if (isInStaffMode(player.getUniqueId())) return staffs.get(player.getUniqueId());
        return null;
    }

    public boolean isInStaffMode(UUID uuid) {
        if (staffs.containsKey(uuid)) return true;
        else return holiday.getProfileHandler().getByUUIDFor5Minutes(uuid).isInStaffMode();
    }

    public boolean isInStaffMode(Player player) {
        return isInStaffMode(player.getUniqueId());
    }

    public boolean canSee(Player viewer, Player player) {
        ProfileHandler ph = holiday.getProfileHandler();
        Profile vProfile = ph.getByPlayer(viewer);
        Profile pProfile = ph.getByPlayer(player);

        if (vProfile.isInStaffMode() && pProfile.isInStaffMode()) return true;
        else if (pProfile.isInStaffMode() && getStaffPlayer(player).isInVanish()) return false;
        else return viewer.canSee(player);

    }

    public void init(Player player) {
        if (!holiday.getSettings().getBoolean("STAFFMODE.ENABLED")) return;
        if (!hasPerm(player)) return;
        if (staffs.containsKey(player.getUniqueId())) return;

        StaffData data = new StaffData(player);

        data.inVanish = holiday.getSettings().getBoolean("STAFFMODE.VANISHONENABLE");
        data.location = player.getLocation();
        data.invContents = player.getInventory().getContents();
        data.armorContents = player.getInventory().getArmorContents();
        data.effects = player.getActivePotionEffects();
        data.level = player.getLevel();
        data.gameMode = player.getGameMode();
        data.flying = player.getAllowFlight();
        data.xp = player.getExp();
        data.health = player.getHealth();

        PlayerUtil.clearPlayer(player);
        updateProfile(player, true);
        player.setAllowFlight(true);
        Tasks.runLater(() -> data.updateItems(true), 5L);


        Bukkit.getServer().getPluginManager().callEvent(new StaffModeEnterEvent(player));
        player.setGameMode(GameMode.CREATIVE);
        staffs.put(player.getUniqueId(), data);

        if (holiday.isLunarEnabled() && LunarClientAPI.getInstance().isRunningLunarClient(player)) LunarClientAPI.getInstance().giveAllStaffModules(player);
    }

    public void handleFreeze(Player player) {
        if (player.hasMetadata("frozen")) {
            player.removeMetadata("frozen", holiday);
            CC.sendFrozenMessage(player, false);
            PlayerUtil.freeze(player, false);
        } else {
            player.setMetadata("frozen", new FixedMetadataValue(holiday, true));
            CC.sendFrozenMessage(player, true);
            PlayerUtil.freeze(player, true);
        }
    }

    public boolean hasPerm(Player player) {
        return player.hasPermission("holiday.staffmode") || player.hasPermission("*") || player.hasPermission("*.*") || player.hasPermission("holiday.*");
    }

    public void destroy(Player player) {

        if (staffs.getOrDefault(player.getUniqueId(), null) == null) return;
        StaffData data = staffs.get(player.getUniqueId());

        data.inVanish = false;
        data.updateVisibility();
        PlayerUtil.clearPlayer(player);

        if (holiday.getSettings().getBoolean("STAFFMODE.TELEPORTBACK")) player.teleport(data.location);
        player.getInventory().setArmorContents(data.armorContents);
        player.getInventory().setContents(data.invContents);
        player.setLevel(data.level);
        player.setExp(data.xp);
        player.setHealth(data.health);
        player.setGameMode(data.gameMode);
        player.addPotionEffects(data.effects);
        player.setAllowFlight(data.flying);
        updateProfile(player, false);

        Bukkit.getServer().getPluginManager().callEvent(new StaffModeLeaveEvent(player));
        staffs.remove(player.getUniqueId());

        if (holiday.isLunarEnabled() && LunarClientAPI.getInstance().isRunningLunarClient(player)) LunarClientAPI.getInstance().disableAllStaffModules(player);

        if (holiday.isLunarEnabled() && holiday.getSettings().getBoolean("LUNAR.NAMETAGS")) {
            Bukkit.getOnlinePlayers().forEach(target -> LunarClientAPI.getInstance().resetNametag(player, target));
        }
    }

    public void destroyWithoutSave(Player player) {
        if (staffs.getOrDefault(player.getUniqueId(), null) == null) return;
        StaffData data = staffs.get(player.getUniqueId());

        data.inVanish = false;
        data.updateVisibility();
        PlayerUtil.clearPlayer(player);
        if (holiday.getSettings().getBoolean("STAFFMODE.TELEPORTBACK")) player.teleport(data.location);
        player.getInventory().setArmorContents(data.armorContents);
        player.getInventory().setContents(data.invContents);
        player.setLevel(data.level);
        player.setGameMode(data.gameMode);
        player.setExp(data.xp);
        player.setHealth(data.health);
        player.addPotionEffects(data.effects);
        player.setAllowFlight(data.flying);
        staffs.remove(player.getUniqueId());

        if (holiday.isLunarEnabled() && LunarClientAPI.getInstance().isRunningLunarClient(player)) LunarClientAPI.getInstance().disableAllStaffModules(player);

        if (holiday.isLunarEnabled() && holiday.getSettings().getBoolean("LUNAR.NAMETAGS")) {
            Bukkit.getOnlinePlayers().forEach(target -> LunarClientAPI.getInstance().resetNametag(player, target));
        }
    }

    private void updateProfile(Player player, boolean enabled) {
        Profile profile = Holiday.getInstance().getProfileHandler().getByPlayer(player);
        profile.setInStaffMode(enabled);
        profile.save();
    }

    @Getter @Setter @RequiredArgsConstructor
    public static class StaffData {
        private final Player player;
        private boolean inVanish, flying;

        private Location location;
        private ItemStack[] invContents, armorContents;
        private Collection<PotionEffect> effects;
        private int level;
        private float xp;
        private double health;
        private GameMode gameMode;

        public void vanish() {
            this.inVanish = !inVanish;
            updateVisibility();
            staffs.remove(player.getUniqueId());
            staffs.put(player.getUniqueId(), this);

            if (inVanish) {
                player.sendMessage(CC.translate("&aYou are now vanished"));
            } else {
                player.sendMessage(CC.translate("&cYou are no longer vanished."));
            }

            updateItems(false);
        }

        public void updateVisibility() {
            Bukkit.getServer().getPluginManager().callEvent(new StaffUpdateVisibilityEvent(player));
            updateItems(false);
        }

        public void updateVisibilityFlicker() {
            Bukkit.getServer().getPluginManager().callEvent(new StaffUpdateVisibilityEvent(player));
        }

        public void updateItems(boolean first) {
            if (first) {
                player.getInventory().setHeldItemSlot(4);
                player.getInventory().setItem(4, Items.FREEZE);
                player.getInventory().setItem(0, Items.INVSEE);
                player.getInventory().setItem(1, Items.BETTER_VIEW);
                player.getInventory().setItem(7, Items.STAFF_LIST);
                player.getInventory().setItem(8, Items.GO_VIS);
            } else {
                if (inVanish) {
                    player.getInventory().setItem(8, Items.GO_VIS);
                } else {
                    player.getInventory().setItem(8, Items.GO_INVIS);
                }
            }
            player.updateInventory();
            staffs.remove(player.getUniqueId());
            staffs.put(player.getUniqueId(), this);
        }
    }

}
