package me.andyreckt.holiday.bukkit.commands;

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
import me.andyreckt.holiday.core.util.json.GsonProvider;
import org.bukkit.command.CommandSender;

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

        HolidayAPI.getUnsafeAPI().getMidnight().removeAll("uuid-cache");
        HolidayAPI.getUnsafeAPI().getUserManager().getProfiles().forEach((uuid, profile) ->
                HolidayAPI.getUnsafeAPI().getMidnight().cache("uuid-cache", uuid.toString(), profile.getName()));
    }

    @SubCommand(names = "server", description = "Server debug", async = true)
    public void serverDebugging(CommandSender sender) {
        if (!Logger.DEV) {
            sender.sendMessage(CC.translate("&cThis command is not available in production"));
            return;
        }
        Logger.debug(GsonProvider.GSON.toJson(Holiday.getInstance().getApi().getServer(Holiday.getInstance().getThisServer().getServerId())));

    }
}

