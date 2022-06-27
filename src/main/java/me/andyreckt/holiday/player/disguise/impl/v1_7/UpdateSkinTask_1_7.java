package me.andyreckt.holiday.player.disguise.impl.v1_7;


import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.utils.GameProfileUtil;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

/**
 * Disguise Class - Edited to fit my needs and 2021
 * @author ConaxGames
 */
@RequiredArgsConstructor
public class UpdateSkinTask_1_7 extends BukkitRunnable {

	private final Holiday plugin;
	private final Player player;
	private final GameProfile newProfileData;
	private final String displayName;

	@Override
	public void run() {

		try {
			final Object entityPlayer = player.getClass().getMethod("getHandle", (Class<?>[])null).invoke(player);
			final Class<?> entityHuman = entityPlayer.getClass().getSuperclass();
			final int maxVersion = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].replaceAll("(v|R[0-9]+)", "").split("_")[0]);
			final int minVersion = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].replaceAll("(v|R[0-9]+)", "").split("_")[1]);
			Field field;
			if (maxVersion >= 1 && minVersion >= 9) {
				field = entityHuman.getDeclaredField("bS");
			}
			else {
				field = entityHuman.getDeclaredField("bH");
			}
			field.setAccessible(true);

			GameProfile currentProfile = (GameProfile) field.get(entityPlayer);

			currentProfile.getProperties().clear();
			for (Property property : this.newProfileData.getProperties().values()) {
				currentProfile.getProperties().put(property.getName(), property);
			}

			GameProfileUtil.v1_7.setName(currentProfile, this.displayName);

			field.set(entityPlayer, currentProfile);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		this.sendPlayerUpdate();
	}

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

		final Entity vehicle = player.getVehicle();
		if (vehicle != null) {
			vehicle.eject();
		}

		this.sendPackets();

		Holiday.getInstance().getNmsHandler().updatePlayer(this.player);
		player.setDisplayName(this.displayName);

	}

	private void sendPackets() {

		Holiday.getInstance().getNmsHandler().removeExecute(this.player);
		Holiday.getInstance().getNmsHandler().addExecute(this.player);
		Holiday.getInstance().getNmsHandler().respawnPlayer(this.player);

	}
}

