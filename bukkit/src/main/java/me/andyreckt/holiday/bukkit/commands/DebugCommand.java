package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.andyreckt.holiday.api.user.IGrant;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.Logger;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Conditions("dev")
@CommandAlias("debug")
@CommandPermission("core.command.debug")
public class DebugCommand extends BaseCommand {
    @HelpCommand
    @Syntax("[page]")
    public void doHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("profile")
    @CommandCompletion("@players")
    public void profileDebugging(CommandSender sender, @Name("profile") Profile profile) {
        Logger.debug(GsonProvider.GSON.toJson(profile));
    }


    @CommandCompletion("@ranks")
    @Subcommand("rank")
	public void rankDebugging(CommandSender sender, @Name("rank") IRank rank) {
		Logger.debug(GsonProvider.GSON.toJson(rank));
    }


    @Subcommand("grants")
    @CommandCompletion("@players")
	public void grantsDebugging(CommandSender sender, @Name("profile") Profile profile) {
		List<IGrant> grants = Holiday.getInstance().getApi().getGrants(profile.getUuid());
        Logger.debug(GsonProvider.GSON.toJson(grants));
    }


    @Subcommand("raccordtoredis")
    public void raccordToRedis(CommandSender sender) {

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


    @Subcommand("server")
    public void serverDebugging(CommandSender sender) {
        Logger.debug(GsonProvider.GSON.toJson(Holiday.getInstance().getApi().getServer(Holiday.getInstance().getThisServer().getServerId())));

    }


    @Subcommand("loadedprofilesamount")
    public void loadedProfilesAmount(CommandSender sender) {
        sender.sendMessage("Loaded profiles amount: " + HolidayAPI.getUnsafeAPI().getUserManager().getProfiles().size());
    }


    @Private
    @Subcommand("db")
    public void dbDebugging(CommandSender sender, @Default("self") Profile profile) {
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

