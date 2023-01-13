package me.andyreckt.holiday.staff.user;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

@Getter @Setter
public class PlayerData {
    private Location location;
    private ItemStack[] invContents, armorContents;
    private Collection<PotionEffect> effects;
    private int level;
    private float xp;
    private double health;
    private GameMode gameMode;
    private boolean flying;
    private boolean allowFlight;
    private float flySpeed, walkSpeed;

    public PlayerData(Player player) {
        this.location = player.getLocation();
        this.invContents = player.getInventory().getContents();
        this.armorContents = player.getInventory().getArmorContents();
        this.effects = player.getActivePotionEffects();
        this.level = player.getLevel();
        this.xp = player.getExp();
        this.health = player.getHealth();
        this.gameMode = player.getGameMode();
        this.flying = player.isFlying();
        this.allowFlight = player.getAllowFlight();
        this.flySpeed = player.getFlySpeed();
        this.walkSpeed = player.getWalkSpeed();
    }
}
