package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.Tasks;
import me.andyreckt.holiday.utils.TimeUtil;
import me.andyreckt.sunset.annotations.Command;
import me.andyreckt.sunset.annotations.MainCommand;
import me.andyreckt.sunset.annotations.Param;
import me.andyreckt.sunset.annotations.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@MainCommand(names = "chat",
		permission = "holiday.chat",
		description = "Chat management commands.",
		usage = "/chat <clear|slow|unslow|mute>",
		helpCommand = "help")
public class ChatCommands {

	@SubCommand(names = "help", async = true)
	public static void help(CommandSender sender) {
		String[] message = {
				"&cUsage: /chat clear",
				"&cUsage: /chat mute",
				"&cUsage: /chat slow (duration)",
				"&cUsage: /chat unslow",
		};

		for (String s: message) {
			sender.sendMessage(CC.translate(s));
		}
	}
	@SubCommand(names = "clear", permission = "holiday.chat.clear", async = true, usage = "/chat clear")
	@Command(names = "clearchat", permission = "holiday.chat.clear", async = true)
	public static void clear(CommandSender sender) {
		StringBuilder sb = new StringBuilder(" ");
		for (int i = 0; i < 1000; i++) {

			Holiday.getInstance().getProfileHandler().getOnlineProfiles().forEach(profile -> {
				if (!profile.isStaff()) {
					Player player = profile.getPlayer();
					if (player != null) {
						player.sendMessage(CC.translate(sb.toString()));
					}
				}
			});
			sb.append(" ");
		}
		Bukkit.broadcastMessage(Holiday.getInstance().getMessages().getString("COMMANDS.CHAT.GLOBAL.CLEAR"));
		Holiday.getInstance().getProfileHandler().getOnlineProfiles().forEach(profile -> {
			if (profile.isStaff()) {
				Player player = profile.getPlayer();
				if (player != null) {
					player.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.CHAT.STAFF.CLEAR").replace("<player>", Holiday.getInstance().getProfileHandler().getByCommandSender(sender).getNameWithColor()));
				}
			}
		});
	}

	@SubCommand(names = "mute", permission = "holiday.chat.mute", async = true, usage = "/chat mute")
	@Command(names = {"mutechat"}, permission = "holiday.chat.mute", async = true)
	public static void mutechat(CommandSender sender) {
		if(!Holiday.getInstance().getChatHandler().isChatMuted()) {
			Holiday.getInstance().getChatHandler().setChatMuted(true);
			Bukkit.broadcastMessage(Holiday.getInstance().getMessages().getString("COMMANDS.CHAT.GLOBAL.MUTE"));

			Holiday.getInstance().getProfileHandler().getOnlineProfiles().forEach(profile -> {
				if (profile.isStaff()) {
					Player player = profile.getPlayer();
					if (player != null) {
						player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("COMMANDS.CHAT.STAFF.MUTE").replace("<player>", Holiday.getInstance().getProfileHandler().getByCommandSender(sender).getNameWithColor())));
					}
				}
			});

		} else {
			Holiday.getInstance().getChatHandler().setChatMuted(false);
			Bukkit.broadcastMessage(Holiday.getInstance().getMessages().getString("COMMANDS.CHAT.GLOBAL.UNMUTE"));

			Holiday.getInstance().getProfileHandler().getOnlineProfiles().forEach(profile -> {
				if (profile.isStaff()) {
					Player player = profile.getPlayer();
					if (player != null) {
						player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("COMMANDS.CHAT.STAFF.UNMUTE").replace("<player>", Holiday.getInstance().getProfileHandler().getByCommandSender(sender).getNameWithColor())));
					}
				}
			});
		}
	}

	@SubCommand(names = "slow", permission = "holiday.chat.slow", async = true, usage = "/chat slow (duration)")
	@Command(names = {"slowchat"}, permission = "holiday.chat.slow", async = true)
	public static void slowchat(CommandSender sender, @Param(name = "time") String duration){

		long time = TimeUtil.getDuration(duration);
		if(time == 0L || time == -1L) {
			sender.sendMessage(CC.translate("&cThis time is not valid."));
			return;
		}
		Holiday.getInstance().getChatHandler().setChatDelay(time);
		Bukkit.broadcastMessage(Holiday.getInstance().getMessages().getString("COMMANDS.CHAT.GLOBAL.SLOW").replace("<time>", duration));

		Holiday.getInstance().getProfileHandler().getOnlineProfiles().forEach(profile -> {
			if (profile.isStaff()) {
				Player player = profile.getPlayer();
				if (player != null) {
					player.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.CHAT.STAFF.SLOW").replace("<player>", Holiday.getInstance().getProfileHandler().getByCommandSender(sender).getNameWithColor()));
				}
			}
		});
	}

	@SubCommand(names = {"unslow"}, permission = "holiday.slowchat", async = true, usage = "/chat unslow")
	@Command(names = {"unslowchat"}, permission = "holiday.slowchat", async = true)
	public static void unslowchat(CommandSender sender){

		Holiday.getInstance().getChatHandler().setChatDelay(0L);
		Bukkit.broadcastMessage(Holiday.getInstance().getMessages().getString("COMMANDS.CHAT.GLOBAL.RESET"));

		Holiday.getInstance().getProfileHandler().getOnlineProfiles().forEach(profile -> {
			if (profile.isStaff()) {
				Player player = profile.getPlayer();
				if (player != null) {
					player.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.CHAT.STAFF.RESET").replace("<player>", Holiday.getInstance().getProfileHandler().getByCommandSender(sender).getNameWithColor()));
				}
			}
		});
	}


}