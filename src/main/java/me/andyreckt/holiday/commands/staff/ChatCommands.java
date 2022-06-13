package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.Tasks;
import me.andyreckt.holiday.utils.TimeUtil;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommands {

	@Command(names = {"clearchat"}, perm = "holiday.clearchat", async = true)
	public static void execute(CommandSender sender) {
		StringBuilder sb = new StringBuilder(" ");
		Tasks.runAsync(() -> {
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
		});
	}

	@Command(names = {"mutechat", "chat mute"}, perm = "holiday.mutechat", async = true)
	public static void mutechat(CommandSender sender) {
		if(!Holiday.getInstance().getChatHandler().isChatMuted()) {
			Holiday.getInstance().getChatHandler().setChatMuted(true);
			Bukkit.broadcastMessage(Holiday.getInstance().getMessages().getString("COMMANDS.CHAT.GLOBAL.MUTE"));

			Holiday.getInstance().getProfileHandler().getOnlineProfiles().forEach(profile -> {
				if (profile.isStaff()) {
					Player player = profile.getPlayer();
					if (player != null) {
						player.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.CHAT.STAFF.MUTE").replace("<player>", Holiday.getInstance().getProfileHandler().getByCommandSender(sender).getNameWithColor()));
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
						player.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.CHAT.STAFF.UNMUTE").replace("<player>", Holiday.getInstance().getProfileHandler().getByCommandSender(sender).getNameWithColor()));
					}
				}
			});
		}
	}

	@Command(names = {"slowchat", "chat slow"}, perm = "holiday.slowchat", async = true)
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

	@Command(names = {"unslowchat", "chat unslow"}, perm = "holiday.slowchat", async = true)
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