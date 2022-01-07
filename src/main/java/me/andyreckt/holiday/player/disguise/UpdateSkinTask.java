package me.andyreckt.holiday.player.disguise;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.utils.GameProfileUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

/**
 * Disguise Class - Edited to fit my needs and 2021
 * @author ConaxGames
 */
@RequiredArgsConstructor
public class UpdateSkinTask extends BukkitRunnable {

	private final Holiday plugin;
	private final Player player;
	private final GameProfile newProfileData;
	private final String displayName;

	@Override
	public void run() {
		final EntityPlayer entityPlayer = ((CraftPlayer) this.player).getHandle();

	//	this.setPlayerNames();

		try {
			Field field = EntityHuman.class.getDeclaredField("bH");
			field.setAccessible(true);

			GameProfile currentProfile = (GameProfile) field.get(entityPlayer);

			currentProfile.getProperties().clear();
			for (Property property : this.newProfileData.getProperties().values()) {
				currentProfile.getProperties().put(property.getName(), property);
			}

			GameProfileUtil.setName(currentProfile, this.displayName);

			field.set(entityPlayer, currentProfile);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		this.sendPlayerUpdate();
	}
	/*
	private void setPlayerNames() {
		MinecraftServer.getServer().getPlayerList().removeFromPlayerNames(player.getName());
		MinecraftServer.getServer().getPlayerList().setPlayerName(displayName, ((CraftPlayer) player).getHandle());
	}*/

	private void sendPlayerUpdate() {
		new BukkitRunnable() {
			@Override
			public void run() {
				sendUpdateToPlayer();

				plugin.getServer().getOnlinePlayers().stream()
				      .filter(other -> !other.equals(player))
				      .filter(other -> other.canSee(player))
				      .forEach(other -> {
					      other.hidePlayer(player);
					      other.showPlayer(player);
				      });
			}
		}.runTask(this.plugin);
	}

	private void sendUpdateToPlayer() {
		final Entity vehicle = this.player.getVehicle();
		if (vehicle != null) {
			vehicle.eject();
		}

		this.sendPackets();

		this.player.updateInventory();
		this.player.setGameMode(this.player.getGameMode());

		PlayerInventory inventory = this.player.getInventory();
		inventory.setHeldItemSlot(inventory.getHeldItemSlot());

		double oldHealth = this.player.getHealth();

		int oldFood = this.player.getFoodLevel();
		float oldSat = this.player.getSaturation();
		this.player.setFoodLevel(20);
		this.player.setFoodLevel(oldFood);
		this.player.setSaturation(5.0F);
		this.player.setSaturation(oldSat);

		this.player.setMaxHealth(this.player.getMaxHealth());

		this.player.setHealth(20.0F);
		this.player.setHealth(oldHealth);

		float experience = this.player.getExp();
		int totalExperience = this.player.getTotalExperience();
		this.player.setExp(experience);
		this.player.setTotalExperience(totalExperience);

		this.player.setWalkSpeed(this.player.getWalkSpeed());
		this.player.setDisplayName(this.displayName);
	}

	private void sendPackets() {
		final EntityPlayer entityPlayer = ((CraftPlayer) this.player).getHandle();
		Location previousLocation = this.player.getLocation().clone();

		entityPlayer.playerConnection.sendPacket(
				new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
		entityPlayer.playerConnection.sendPacket(
				new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
		entityPlayer.playerConnection.sendPacket(
				new PacketPlayOutRespawn(entityPlayer.getWorld().worldProvider.getDimension(),
						entityPlayer.getWorld().worldData.getDifficulty(),
						entityPlayer.getWorld().worldData.getType(),
						WorldSettings.EnumGamemode.valueOf(entityPlayer.getBukkitEntity().getGameMode().name())));
		this.player.teleport(previousLocation);
	}
}

