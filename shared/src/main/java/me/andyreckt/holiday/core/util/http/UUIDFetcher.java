package me.andyreckt.holiday.core.util.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class UUIDFetcher {
	private static final int PROFILES_PER_REQUEST = 50;

	private static final String PROFILE_URL = "https://api.mojang.com/users/profiles/minecraft/";
	private static final String NAME_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";


	private static UUID getUUID(String id) {
		return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
	}

	public static byte[] toBytes(UUID uuid) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
		byteBuffer.putLong(uuid.getMostSignificantBits());
		byteBuffer.putLong(uuid.getLeastSignificantBits());
		return byteBuffer.array();
	}

	public static UUID fromBytes(byte[] array) {
		if (array.length != 16)
			throw new IllegalArgumentException("Illegal byte array length: " + array.length);
		ByteBuffer byteBuffer = ByteBuffer.wrap(array);
		long mostSignificant = byteBuffer.getLong();
		long leastSignificant = byteBuffer.getLong();
		return new UUID(mostSignificant, leastSignificant);
	}

	public static UUID getSync(String name) {
		try {
			String urlString = PROFILE_URL + name;
			URL url = new URL(urlString);

			InputStreamReader reader = new InputStreamReader(url.openStream());
			JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
			String id = object.get("id").getAsString();

			return getUUID(id);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getNameSync(UUID uuid) {
		return getNameSync(uuid.toString());
	}

	public static String getNameSync(String uuid) {
		try {
			String urlString = NAME_URL + uuid.replace("-", "");
			URL url = new URL(urlString);

			InputStreamReader reader = new InputStreamReader(url.openStream());
			JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();

			return object.get("name").getAsString();
		} catch (Exception e) {
			return null;
		}
	}

	public static CompletableFuture<String> getName(UUID uuid) {
		return getName(uuid.toString());
	}

	public static CompletableFuture<String> getName(String uuid) {
		return CompletableFuture.supplyAsync(() -> getNameSync(uuid));
	}


	public static CompletableFuture<UUID> get(String name) {
		return CompletableFuture.supplyAsync(() -> getSync(name));
	}

	public static CompletableFuture<Map<String, UUID>> get(List<String> names) {
		return CompletableFuture.supplyAsync(() -> {
			Map<String, UUID> uuidMap = new HashMap<>();
			try {
				while (!names.isEmpty()) {
					List<String> subList = names.size() > PROFILES_PER_REQUEST ? names.subList(0, PROFILES_PER_REQUEST) : names;
					names.removeAll(subList);
					for (String name : subList) {
						uuidMap.put(name, getSync(name));
					}
					Thread.sleep(100L);
				}
				return uuidMap;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	public static CompletableFuture<Map<String, UUID>> get(String... names) {
		return get(Arrays.asList(names));
	}

	public static Map<String, UUID> getSync(List<String> names) {
		Map<String, UUID> uuidMap = new HashMap<>();
		try {
			while (!names.isEmpty()) {
				List<String> subList = names.size() > PROFILES_PER_REQUEST ? names.subList(0, PROFILES_PER_REQUEST) : names;
				names.removeAll(subList);
				for (String name : subList) {
					uuidMap.put(name, getSync(name));
				}
				Thread.sleep(100L);
			}
			return uuidMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}