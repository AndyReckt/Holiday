package me.andyreckt.holiday.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.Holiday;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class PlayerList { //PVPTEMPLE

	private final List<Player> players;

	public static PlayerList getVisiblyOnline(CommandSender sender) {
		return getOnline().visibleTo(sender);
	}

	public static PlayerList getOnline() {
		return new PlayerList(new ArrayList<>(Holiday.getInstance().getServer().getOnlinePlayers()));
	}

	public PlayerList visibleTo(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			this.players.removeIf(other -> other != player && (Holiday.getInstance().getProfileHandler().getByPlayer(other).isVanished() && !Holiday.getInstance().getProfileHandler().getByPlayer(player).isStaff()));
		}
		return this;
	}

	public PlayerList canSee(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			this.players.removeIf(other -> other == player && (Holiday.getInstance().getProfileHandler().getByPlayer(player).isVanished() && !Holiday.getInstance().getProfileHandler().getByPlayer(other).isStaff()));
		}
		return this;
	}

	public PlayerList visibleRankSorted() {
		this.players.sort(PlayerUtil.RANK_ORDER);
		return this;
	}

	public List<String> asColoredNames() {
		return this.players.stream()
				.map(Player::getUniqueId)
				.map(uid -> Holiday.getInstance().getProfileHandler().getByUUID(uid))
				.map(profile -> profile.getDisplayNameWithColorAndVanish() + CC.GRAY)
				.collect(Collectors.toList());
	}

}
