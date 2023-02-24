package me.andyreckt.holiday.bukkit.commands;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.andyreckt.holiday.api.user.IGrant;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.Logger;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.MainCommand;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.SubCommand;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.signature.qual.SignatureUnknown;

import java.util.List;


@MainCommand(names = "debug", description = "Debug command", permission = Perms.DEBUG)
public class DebugCommand {
    @SubCommand(names = "profile", description = "Profile Debug", async = true)
    public void profileDebugging(CommandSender sender, @Param(name = "profile") Profile profile) {
        if (!Logger.DEV) {
            sender.sendMessage(CC.translate("&cThis command is not available in production"));
            return;
        }
        Logger.debug(GsonProvider.GSON.toJson(profile));
    }

	@SubCommand(names = "rank", description = "Rank debug", async = true)
	public void rankDebugging(CommandSender sender, @Param(name = "rank") IRank rank) {
        if (!Logger.DEV) {
            sender.sendMessage(CC.translate("&cThis command is not available in production"));
            return;
        }
		Logger.debug(GsonProvider.GSON.toJson(rank));
    }

	@SubCommand(names = "grants", description = "Player grants Debug", async = true)
	public void grantsDebugging(CommandSender sender, @Param(name = "profile") Profile profile) {
        if (!Logger.DEV) {
            sender.sendMessage(CC.translate("&cThis command is not available in production"));
            return;
        }
		List<IGrant> grants = Holiday.getInstance().getApi().getGrants(profile.getUuid());
        Logger.debug(GsonProvider.GSON.toJson(grants));
    }

    @SubCommand(names = "raccordtoredis", description = "uuid-cache updating", async = true)
    public void raccordToRedis(CommandSender sender) {
        if (!Logger.DEV) {
            sender.sendMessage(CC.translate("&cThis command is not available in production"));
            return;
        }

        HolidayAPI.getUnsafeAPI().runRedisCommand(redis -> {
            HolidayAPI.getUnsafeAPI().getUserManager().getAllProfilesDb().whenComplete((profiles, throwable) -> {
                profiles.forEach((uuid, profile) -> {
                    redis.hdel("uuid-cache", uuid.toString());
                    redis.hset("uuid-cache", uuid.toString(), profile.getName());
                });
            });
            return null;
        });
    }

    @SubCommand(names = "server", description = "Server debug", async = true)
    public void serverDebugging(CommandSender sender) {
        if (!Logger.DEV) {
            sender.sendMessage(CC.translate("&cThis command is not available in production"));
            return;
        }
        Logger.debug(GsonProvider.GSON.toJson(Holiday.getInstance().getApi().getServer(Holiday.getInstance().getThisServer().getServerId())));

    }

    @SubCommand(names = "loadedprofilesamount", description = "Loaded profiles amount", async = true)
    public void loadedProfilesAmount(CommandSender sender) {
        if (!Logger.DEV) {
            sender.sendMessage(CC.translate("&cThis command is not available in production"));
            return;
        }
        sender.sendMessage("Loaded profiles amount: " + HolidayAPI.getUnsafeAPI().getUserManager().getProfiles().size());
    }

    @SubCommand(names = "db", description = "Database debug")
    public void dbDebugging(CommandSender sender, @Param(name = "player", baseValue = "self") Profile profile) {
        if (!Logger.DEV) {
            sender.sendMessage(CC.translate("&cThis command is not available in production"));
            return;
        }
        long savestart = System.currentTimeMillis();
        HolidayAPI.getUnsafeAPI().getMongoManager().getProfiles().replaceOne(
                Filters.eq("_id", profile.getUuid().toString()),
                Document.parse(GsonProvider.GSON.toJson((UserProfile) profile)),
                new ReplaceOptions().upsert(true)
        );
        long saveend = System.currentTimeMillis();
        long loadstart = System.currentTimeMillis();
        Document doc = HolidayAPI.getUnsafeAPI().getMongoManager().getProfiles().find(Filters.eq("_id", profile.getUuid().toString())).first();
        Profile profile1 = HolidayAPI.getUnsafeAPI().getUserManager().loadProfile(doc);
        long loadend = System.currentTimeMillis();
        sender.sendMessage("Saving Profile: " + (saveend - savestart) + "ms");
        sender.sendMessage("Loading Profile: " + (loadend - loadstart) + "ms");
    }
}

