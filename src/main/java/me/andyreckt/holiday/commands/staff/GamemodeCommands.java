package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommands {


	@Command(names = {"gmc", "gm c", "gamemode c", "gamemode creative", "gm 1", "gamemode 1", "gm1"}, perm = "holiday.gamemode")
	public static void gmc(CommandSender sender, @Param(name = "target", defaultValue = "self") Player target) {
		target.setGameMode(GameMode.CREATIVE);
		sendMessage(sender, target);
	}

	@Command(names = {"gma", "gm a", "gamemode a", "gamemode adventure", "gm 2", "gamemode 2", "gm2"}, perm = "holiday.gamemode")
	public static void gma(CommandSender sender, @Param(name = "target", defaultValue = "self") Player target) {
		target.setGameMode(GameMode.ADVENTURE);
		sendMessage(sender, target);
	}

	@Command(names = {"gms", "gm s", "gamemode s", "gamemode survival", "gm 0", "gamemode 0"}, perm = "holiday.gamemode")
	public static void gms(CommandSender sender, @Param(name = "target", defaultValue = "self") Player target) {
		target.setGameMode(GameMode.SURVIVAL);
		sendMessage(sender, target);
	}

	@Command(names = {"gmsp", "gm sp", "gamemode sp", "gamemode spectator", "gm 3", "gamemode 3"}, perm = "holiday.gamemode")
	public static void gmsp(CommandSender sender, @Param(name = "target", defaultValue = "self") Player target) {
		target.setGameMode(GameMode.SPECTATOR);
		sendMessage(sender, target);
	}

	@Command(names = {"gm", "gamemode"}, perm = "holiday.gamemode")
	public static void gm(CommandSender sender) {
		sender.sendMessage(CC.translate("&cUsage: /gamemode <gamemode> [player]"));
	}

	static void sendMessage(CommandSender sender, Player target) {
		BasicConfigurationFile messages = Holiday.getInstance().getMessages();

		if (sender instanceof Player) {
			if (target == (sender)) sender.sendMessage(messages.getString("COMMANDS.GAMEMODE.TARGET").replace("<gamemode>", target.getGameMode().name()));
			else {
				target.sendMessage(CC.translate("&eYour gamemode has been set to &d" + target.getGameMode().name()));
				sender.sendMessage(messages.getString("COMMANDS.GAMEMODE.SENDER").replace("<gamemode>", target.getGameMode().name()).replace("<target>", target.getName()));
			}
		} else {
			target.sendMessage(messages.getString("COMMANDS.GAMEMODE.TARGET").replace("<gamemode>", target.getGameMode().name()));
			sender.sendMessage(messages.getString("COMMANDS.GAMEMODE.SENDER").replace("<gamemode>", target.getGameMode().name()).replace("<target>", target.getName()));
		}
	}
}