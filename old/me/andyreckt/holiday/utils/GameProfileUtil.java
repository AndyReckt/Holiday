package me.andyreckt.holiday.utils;

import com.mojang.authlib.GameProfile;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @since 10/9/2017
 */
public class GameProfileUtil {

	public static class v1_7 {
		public static net.minecraft.util.com.mojang.authlib.GameProfile clone(net.minecraft.util.com.mojang.authlib.GameProfile gameProfile) {
			net.minecraft.util.com.mojang.authlib.GameProfile newProfile = new net.minecraft.util.com.mojang.authlib.GameProfile(gameProfile.getId(), gameProfile.getName());
			newProfile.getProperties().putAll(gameProfile.getProperties());
			return newProfile;
		}

		public static net.minecraft.util.com.mojang.authlib.GameProfile setName(net.minecraft.util.com.mojang.authlib.GameProfile gameProfile, String newName) {
			try {
				Field modifiersField = Field.class.getDeclaredField("modifiers");

				// wrapping setAccessible
				AccessController.doPrivileged((PrivilegedAction) () -> {
					modifiersField.setAccessible(true);
					return null;
				});

				Field nameField = net.minecraft.util.com.mojang.authlib.GameProfile.class.getDeclaredField("name");
				modifiersField.setInt(nameField, nameField.getModifiers() & ~Modifier.FINAL);
				nameField.setAccessible(true);
				nameField.set(gameProfile, newName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return gameProfile;
		}
	}

	public static class v1_8 {
		public static GameProfile clone(GameProfile gameProfile) {
			GameProfile newProfile = new GameProfile(gameProfile.getId(), gameProfile.getName());
			newProfile.getProperties().putAll(gameProfile.getProperties());
			return newProfile;
		}

		public static GameProfile setName(GameProfile gameProfile, String newName) {
			try {
				Field modifiersField = Field.class.getDeclaredField("modifiers");

				// wrapping setAccessible
				AccessController.doPrivileged((PrivilegedAction) () -> {
					modifiersField.setAccessible(true);
					return null;
				});

				Field nameField = GameProfile.class.getDeclaredField("name");
				modifiersField.setInt(nameField, nameField.getModifiers() & ~Modifier.FINAL);
				nameField.setAccessible(true);
				nameField.set(gameProfile, newName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return gameProfile;
		}
	}



}
