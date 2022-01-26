package me.andyreckt.holiday.utils;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.net.URL;
import java.util.*;


/**
 * Created by Marko on 01.03.2019.
 */
public class PlayerUtil {

    public static Comparator<Player> RANK_ORDER = ((o1, o2) -> {
        Profile p1 = Holiday.getInstance().getProfileHandler().getByUUID(o1.getUniqueId());
        Profile p2 = Holiday.getInstance().getProfileHandler().getByUUID(o2.getUniqueId());
        return - (p1.getDisplayRank().getPriority() - p2.getDisplayRank().getPriority());
    });


    public static void clearPlayer(Player player) {
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
        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
        player.getOpenInventory().getTopInventory().clear();
        player.updateInventory();
    }

    public static void freeze(Player player, boolean value) {
        if(value) {
            player.setGameMode(GameMode.SURVIVAL);
            player.setWalkSpeed(0.0F);
            player.setFlySpeed(0.0F);
            player.setFoodLevel(20);
            player.setSprinting(false);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 200));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 200));
        } else {
            player.setWalkSpeed(0.2F);
            player.setFlySpeed(0.1F);
            player.setFoodLevel(20);
            player.setSprinting(true);
            player.removePotionEffect(PotionEffectType.JUMP);
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        }
    }

    public static boolean hasVotedOnNameMC(UUID uuid) {
        BasicConfigurationFile config = Holiday.getInstance().getSettings();
        try (Scanner scanner = new Scanner(
                new URL("https://api.namemc.com/server/" + config.getString("NETWORK.IP") + "/likes?profile=" + uuid.toString())
                        .openStream()).useDelimiter("\\A")) {
            return Boolean.parseBoolean(scanner.next());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    public static boolean isFullInventory(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

}
