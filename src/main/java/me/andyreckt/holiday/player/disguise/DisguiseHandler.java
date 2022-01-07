package me.andyreckt.holiday.player.disguise;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.database.redis.packet.DisguisePacket;
import me.andyreckt.holiday.other.enums.DisguiseType;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.GameProfileUtil;
import me.andyreckt.holiday.utils.UUIDFetcher;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


/**
 * Disguise Class - Edited to fit our needs and 2021
 * @author ConaxGames
 */
public class DisguiseHandler {

	private final Holiday plugin;
	private final Map<String, GameProfile> skinCache = new HashMap<>();
	@Getter
	private final Map<UUID, GameProfile> originalCache = new HashMap<>();
	@Getter
	private final Map<UUID, DisguiseData> disguiseData = new HashMap<>();

	public static List<String> usedNames = new ArrayList<>();


	public DisguiseHandler(Holiday plugin) {
		this.plugin = plugin;
		MongoUtils.submitToThread(() -> {
			if(MongoUtils.getDisguiseCollection().find(Filters.eq("_id", "names")).first() == null) {
				Document host = new Document("_id", "names")
						.append("list", new ArrayList<String>(500));
				MongoUtils.getDisguiseCollection().replaceOne(Filters.eq("_id", "names"), host, new ReplaceOptions().upsert(true));
			}
			Document doc = (Document) MongoUtils.getDisguiseCollection().find(Filters.eq("_id", "names")).first();
			usedNames.addAll(doc.getList("list", String.class));
		});
	}

	public void disguise(Player player, Rank rank, String skin, String name, boolean sendRequest) throws Exception {
		if(sendRequest) {
			if (Bukkit.getPlayer(name) != null || DisguiseRequest.alreadyUsed(name)) {
				player.sendMessage(CC.RED + "This name is already in use.");
				return;
			}
		}

		GameProfile targetProfile = this.skinCache.get(skin.toLowerCase());

		if(targetProfile == null) {
			UUIDFetcher uuidFetcher = new UUIDFetcher(Collections.singletonList(skin));
			Map<String, UUID> fetched = uuidFetcher.call();

			Optional<UUID> fetchedUuid = fetched.values().stream().findFirst();
			if(!fetchedUuid.isPresent()) {
				targetProfile = this.loadGameProfile(UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7"), "Steve");
			} else {
				targetProfile = this.loadGameProfile(fetchedUuid.get(), skin);
			}

		}

		Profile profile = plugin.getProfileHandler().getByUUID(player.getUniqueId());

		DisguiseData data = getDisguiseData(player.getUniqueId());
		data.disguiseRank = rank;
		data.displayName = name;
		data.skinName = skin;
		data.uuid = profile.getUuid();
		data.lDisplayName = name.toLowerCase();
		profile.setDisguiseData(data);
		profile.save();
		this.disguiseData.put(profile.getUuid(), data);

		if (sendRequest) {
			DisguiseRequest.addDisguise(profile);
			player.sendMessage(CC.translate("&eYou are now disguised to &d" + name + "&e with the skin of &d" + skin + "&e."));
		}

		// Make sure we don't cache another game profile that isn't actually theirs
		if (!this.originalCache.containsKey(player.getUniqueId())) {
			this.originalCache.put(player.getUniqueId(), GameProfileUtil.clone(((CraftPlayer) player).getHandle().getProfile()));
		}

		new UpdateSkinTask(this.plugin, player, targetProfile, name).runTask(this.plugin);
	}

	public void undisguise(Player player, boolean sendRequest) {
		GameProfile originalProfile = this.originalCache.remove(player.getUniqueId());
		if (originalProfile != null) {
			new UpdateSkinTask(this.plugin, player, originalProfile, originalProfile.getName()).runTask(this.plugin);
			Profile mineman = plugin.getProfileHandler().getByUUID(player.getUniqueId());

			if (sendRequest) {
				DisguiseRequest.removeDisguise(mineman);
				player.sendMessage(CC.translate("&eYou are no longer disguised."));
			}

			mineman.setDisguiseData(null);
			mineman.save();
			//mineman.updateTabList(mineman.getRank());

			disguiseData.remove(player.getUniqueId());
			/*
			MinecraftServer.getServer().getPlayerList().setPlayerName(originalProfile.getName(), ((CraftPlayer) player).getHandle());
			MinecraftServer.getServer().getPlayerList().removeFromPlayerNames(player.getName());

			 */
		}
	}

	public GameProfile loadGameProfile(UUID uniqueId, String skinName) {
		GameProfile profile = this.skinCache.get(skinName.toLowerCase());

		BufferedReader reader = null;
		try {
			if (profile == null || !profile.getProperties().containsKey("textures")) {
				URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uniqueId.toString().replace("-", "") + "?unsigned=false");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.addRequestProperty("User-Agent", "Core");
				connection.setDoOutput(true);
				connection.connect();

				if (connection.getResponseCode() == 200) {
					reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder stringBuilder = new StringBuilder();
					reader.lines().forEach(stringBuilder::append);
					JsonObject object = new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();
					skinName = object.get("name").getAsString();
					if (profile == null) {
						profile = new GameProfile(uniqueId, skinName);
					}
					JsonArray array = object.get("properties").getAsJsonArray();
					for (Object obj : array) {
						JsonObject property = (JsonObject) obj;
						String propertyName = property.get("name").getAsString();
						profile.getProperties().put(propertyName,
								new Property(propertyName, property.get("value").getAsString(),
										property.get("signature").getAsString()));
					}

					this.skinCache.put(skinName.toLowerCase(), profile);
					MinecraftServer.getServer().getUserCache().a(profile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ignored) {}
			}
		}

		return profile;
	}

	public boolean isDisguised(UUID uuid) {
		return this.originalCache.containsKey(uuid);
	}

	public boolean isDisguisedMongo(UUID uuid) {
		return getDisguiseData(uuid).displayName() != null;
	}

	public boolean isDisguised(Player player) {
		return this.isDisguised(player.getUniqueId());
	}

	public DisguiseData getDisguiseData(UUID uuid) {
		if(this.disguiseData.containsKey(uuid)) return this.disguiseData.get(uuid);

		DisguiseData data = new DisguiseData();
		Document doc = (Document) MongoUtils.getDisguiseCollection().find(Filters.eq("_id", uuid.toString())).first();
		if(doc != null) {
			data.uuid = UUID.fromString(doc.getString("_id"));
			data.displayName = doc.getString("displayName");
			data.skinName = doc.getString("skinName");
			data.disguiseRank = Holiday.getInstance().getRankHandler().getFromName(doc.getString("disguiseRank"));
			data.lDisplayName = doc.getString("displayName").toLowerCase();
		}
		return data;
	}

	@Getter
	@Setter
	@Accessors(fluent = true)

	public static class DisguiseData {

		String displayName;
		String skinName;
		Rank disguiseRank;
		UUID uuid;
		String lDisplayName;

	}


	public static class DisguiseRequest {

		public static boolean alreadyUsed(String name) {
			return usedNames.contains(name.toLowerCase());
			/*
			Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("displayName", name)).first();
			return document != null;

			 */
		}

		public static void addDisguise(Profile profile) {
			Document document = new Document("_id", profile.getUuid().toString())
					.append("displayName", profile.getDisguiseData().displayName())
					.append("skinName", profile.getDisguiseData().skinName())
					.append("lDisplayName", profile.getDisguiseData().displayName().toLowerCase())
					.append("disguiseRank", profile.getDisguiseData().disguiseRank().getName());

			MongoUtils.submitToThread(() -> MongoUtils.getDisguiseCollection().replaceOne(Filters.eq("_id", profile.getUuid().toString()), document, new ReplaceOptions().upsert(true)));
			Holiday.getInstance().getRedis().sendPacket(new DisguisePacket(profile.getDisguiseData().displayName(), DisguiseType.ADD));
			if(!DisguiseHandler.usedNames.contains(profile.getDisguiseData().displayName().toLowerCase())) DisguiseHandler.usedNames.add(profile.getDisguiseData().displayName().toLowerCase());
			MongoUtils.submitToThread(() -> MongoUtils.getDisguiseCollection().replaceOne(Filters.eq("_id", "names"), new Document("_id", "names")
					.append("list", usedNames)));
		}

		public static void removeDisguise(Profile profile) {
			Document document = (Document) MongoUtils.getProfileCollection().find(Filters.eq("_id", profile.getUuid().toString())).first();
			if(document != null) {
				MongoUtils.submitToThread(() -> MongoUtils.getDisguiseCollection().deleteOne(Filters.eq("_id", profile.getUuid().toString())));
			}
			Holiday.getInstance().getRedis().sendPacket(new DisguisePacket(profile.getDisguiseData().displayName(), DisguiseType.REMOVE));
			MongoUtils.submitToThread(() -> MongoUtils.getDisguiseCollection().replaceOne(Filters.eq("_id", "names"), new Document("_id", "names")
					.append("list", usedNames)));
		}

		public static DisguiseData getDataFromName(String name) {
			Document document = (Document) MongoUtils.getDisguiseCollection().find(Filters.eq("lDisplayName", name.toLowerCase())).first();
			if (document != null) {
				DisguiseData data = new DisguiseData();


				data.disguiseRank(Holiday.getInstance().getRankHandler().getFromName(document.getString("disguiseRank")));
				data.displayName(document.getString("displayName"));
				data.skinName(document.getString("skinName"));
				data.uuid(UUID.fromString(document.getString("_id")));
				data.lDisplayName(document.getString("displayName").toLowerCase());
				return data;
			} else return null;
		}


	}

}
